package com.flyjingfish.module_communication_ksp

import com.flyjingfish.module_communication_annotation.ExposeBean
import com.flyjingfish.module_communication_annotation.ExposeInterface
import com.flyjingfish.module_communication_annotation.ImplementClass
import com.flyjingfish.module_communication_annotation.Route
import com.flyjingfish.module_communication_annotation.RouteParams
import com.flyjingfish.module_communication_annotation.bean.ParamsInfo
import com.flyjingfish.module_communication_annotation.bean.PathInfo
import com.flyjingfish.module_communication_annotation.interfaces.BaseRouter
import com.flyjingfish.module_communication_annotation.interfaces.BaseRouterClass
import com.flyjingfish.module_communication_annotation.interfaces.BindClass
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.FileLocation
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import java.io.File
import java.io.FileInputStream
import java.util.Locale


class CommunicationKspSymbolProcessor(
    private val environment: SymbolProcessorEnvironment,
) : SymbolProcessor {
    private val codeGenerator: CodeGenerator = environment.codeGenerator
    private val logger: KSPLogger = environment.logger
    private lateinit var moduleName :String
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols =
            resolver.getSymbolsWithAnnotation(ImplementClass::class.qualifiedName!!)
        val ret1 = processImplementClass(symbols)
        val ret3 = processExposeInterface(resolver,symbols)
        val ret2 = processExposeBean(resolver)
        val ret4 = processRouteParams(resolver)
        val ret5 = processRoute(resolver)
        val ret = arrayListOf<KSAnnotated>()
        ret.addAll(ret1)
        ret.addAll(ret2)
        ret.addAll(ret3)
        ret.addAll(ret4)
        ret.addAll(ret5)
        return ret
    }

    private fun processImplementClass(symbols: Sequence<KSAnnotated>): List<KSAnnotated> {
        for (symbol in symbols) {
            val annotationMap = getAnnotation(symbol)
            val classMethodMap: MutableMap<String, Any?> =
                annotationMap["@ImplementClass"] ?: continue

            val value: KSType =
                (if (classMethodMap["value"] != null) classMethodMap["value"] as KSType else null)
                    ?: continue
            if (symbol !is KSClassDeclaration){
                continue
            }

            val targetClassName: String =
                (value.declaration.packageName.asString() + "." + value.toString())

            isImplementClass(symbol,targetClassName)

            val implementClassName = ClassName(value.declaration.packageName.asString(), "$value")
            val bindClassName = ClassName.bestGuess(BindClass::class.qualifiedName!!)
            val superinterface = bindClassName.parameterizedBy(implementClassName)
            val className = symbol.packageName.asString() + "." + symbol

            val fileName = "$value\$\$BindClass"
            val typeBuilder = TypeSpec.classBuilder(
                fileName
            ).addModifiers(KModifier.FINAL).addSuperinterface(superinterface)

            val whatsMyName1 = whatsMyName("getImplementClassInstance")
                .addModifiers(KModifier.OVERRIDE)
                .addModifiers(KModifier.FINAL)
                .addModifiers(KModifier.PUBLIC)
                .returns(implementClassName)
            whatsMyName1.addStatement("return %T()",ClassName.bestGuess(className))

            typeBuilder.addFunction(whatsMyName1.build())
            writeToFile(typeBuilder, value.declaration.packageName.asString(),fileName, symbol)
        }
        return symbols.filter { !it.validate() }.toList()
    }
    private fun processExposeBean(resolver: Resolver): List<KSAnnotated> {
        val symbols =
            resolver.getSymbolsWithAnnotation(ExposeBean::class.qualifiedName!!)
        for (symbol in symbols) {
            if (symbol is KSClassDeclaration) {

                val file = File((symbol.location as FileLocation).filePath)

                val fileName =
                    "${symbol}${file.absolutePath.substring(file.absolutePath.lastIndexOf("."))}"

                writeToFile(fileName, symbol, symbol.packageName.asString(), file)
            }

        }
        return symbols.filter { !it.validate() }.toList()
    }
    private fun processRoute(resolver: Resolver): List<KSAnnotated> {
        val symbols =
            resolver.getSymbolsWithAnnotation(Route::class.qualifiedName!!).toList()
        if (symbols.isNotEmpty()){
            val routeModuleName = environment.options["routeModuleName"]
                ?: throw IllegalArgumentException("注意：你还没有给当前 module 设置 communication.export 插件")

            val routeModulePackageName = environment.options["routeModulePackageName"]
                ?: throw IllegalArgumentException("注意：你还没有给当前 module 设置 communication.export 插件")

            val exportEmptyRoute = environment.options["exportEmptyRoute"]
                ?: "false"

            val emptyRoute = exportEmptyRoute == "true"

            var fullName = ""
            val names = routeModuleName.split("-")
            for(token in names){
                if ("" != token){
                    fullName += (token.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    })
                }
            }
            moduleName = fullName

            val clazzClassName = ClassName.bestGuess(Class::class.qualifiedName!!)
            val valueClassName = clazzClassName.parameterizedBy(STAR)

            val mapClassName = ClassName.bestGuess("kotlin.collections.MutableMap")
            val mapInterface = mapClassName.parameterizedBy(ClassName.bestGuess(String::class.qualifiedName!!),
                ClassName.bestGuess(PathInfo::class.qualifiedName!!))

            val registerMapFun = whatsMyName("registerMap")

            val routeClassFile = "$moduleName\$\$RouterClass"
            val classBuilder = TypeSpec.classBuilder(
                routeClassFile
            ).addProperty(
                    PropertySpec.builder("classMap", mapInterface)
                        .addModifiers(KModifier.PRIVATE)
                        .initializer("mutableMapOf()")
                        .build()
                )
                .primaryConstructor(FunSpec.constructorBuilder()
                    .addParameter(ParameterSpec.builder("initClazzMap", Boolean::class)
                        .defaultValue("true")
                        .build())
                    .build())
                .addInitializerBlock(CodeBlock.of("if (initClazzMap){\n" +
                        "      registerMap()\n" +
                        "    }"))
                .addSuperinterface(ClassName.bestGuess(BaseRouterClass::class.qualifiedName!!))
            val routeFile = "$moduleName\$\$Router"
            val routeBuilder = TypeSpec.objectBuilder(
                routeFile
            ).addSuperinterface(ClassName.bestGuess(BaseRouter::class.qualifiedName!!))
            val bindClassName = ClassName.bestGuess(Class::class.qualifiedName!!)
            val classStar = bindClassName.parameterizedBy(STAR)
            val ksFiles = mutableListOf<KSFile>()
            val pathInfoClazz = "com.flyjingfish.module_communication_annotation.bean.PathInfo"
            for (symbol in symbols) {
                val annotationMap = getAnnotation(symbol)
                val className = (symbol as KSClassDeclaration).packageName.asString() + "." + symbol
                val realPath : String = annotationMap["@Route"]?.get("path") as String
                val tag : Int = annotationMap["@Route"]?.get("tag") as Int

                val usePath: String = if (realPath.firstOrNull()?.toString() != "/"){
                    "/$realPath"
                }else{
                    realPath
                }

                val clazzPath: String = if (realPath.firstOrNull()?.toString() == "/"){
                    realPath.substring(1)
                }else{
                    realPath
                }


                val classKey:String
                val routeClassName = if (clazzPath.isNotEmpty()){
                    classKey = clazzPath.replace(".","_").replace("/","_")
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                    classKey
                }else{
                    classKey = className.replace(".","_")
                    "$symbol"
                }


                val paramMap = routeParamsMap[className]

                val paramsInfoStringBuilder = StringBuilder()
                val paramsClazz = mutableListOf<TypeName>()
                paramsClazz.add(ClassName.bestGuess(
                    PathInfo::class.qualifiedName!!
                ))
                paramsClazz.add(ClassName.bestGuess(
                    className
                ))
                paramsClazz.add(ClassName.bestGuess(
                    ParamsInfo::class.qualifiedName!!
                ))
                paramMap?.forEach { (_, value) ->
                    val config = value.annoMap["@RouteParams"]
                    if (config != null){
                        val paramsName : String = config["name"] as String
                        val paramNullable : Boolean = config["nullable"] as Boolean
                        val targetClassName: String = value.className
                        val typeName = value.getTypeClazzName()
                            ?: throw IllegalArgumentException("不支持 $className 的 $paramsName 的类型：$targetClassName")
                        paramsInfoStringBuilder.append("          add(ParamsInfo(\"$paramsName\",%T::class,$paramNullable))\n")
                        paramsClazz.add(typeName)
                    }
                }

                val pathInfoStr = "%T(\"$usePath\",%T::class,$tag,mutableListOf<%T>().apply {\n" +
                        paramsInfoStringBuilder.toString() +
                        "        })"


                val classMapTypeNames = paramsClazz.toTypedArray()
                val paramListStr = "val paramsInfoList = mutableListOf<%T>()"
//                logger.error("paramsInfoStringBuilder=$paramsInfoStringBuilder")
                if (isSubtype(symbol,"android.app.Activity")){
                    val classFunName = "get${classKey}Class"
                    val whatsMyName1 = whatsMyName("go$routeClassName")
                    if (!emptyRoute){
                        registerMapFun.addStatement("classMap[\"$usePath\"] = $pathInfoStr",*classMapTypeNames)
                        classBuilder.addFunction(whatsMyName(classFunName)
                            .returns(classStar.copy(nullable = true))
                            .addStatement("return %T::class.java",ClassName.bestGuess(
                                className
                            ))
                            .build())

                        val activityBuilder = TypeSpec.classBuilder(
                            symbol.toString()
                        ).superclass(ClassName.bestGuess("android.app.Activity"))

                        writeToFile(activityBuilder, symbol.packageName.asString(),symbol.toString(),symbol,true,false)

                        whatsMyName1.addParameter("context",ClassName.bestGuess(
                            "android.content.Context"
                        ))
                        whatsMyName1.addStatement(
                            "val paramMap = mutableMapOf<String,Any?>()"
                        )

                        whatsMyName1.addStatement(
                            "val pathInfo = $pathInfoStr",*classMapTypeNames
                        )

                        whatsMyName1.addStatement(
                            "val routeClazz = `$routeClassFile`(false)",
                            ClassName.bestGuess(
                                "android.content.Intent"
                            )
                        )
                        paramMap?.forEach { (_, value) ->
                            val config = value.annoMap["@RouteParams"]
                            if (config != null){
                                val paramsName : String = config["name"] as String
                                whatsMyName1.addStatement(
                                    "paramMap[\"$paramsName\"] = $paramsName",
                                )
                            }
                        }
                        whatsMyName1.addStatement(
                            "routeClazz.goByPath(\"$usePath\",paramMap,false,pathInfo){"
                        )
                        whatsMyName1.addStatement(
                            "  val intent = %T(context,routeClazz.$classFunName())",
                            ClassName.bestGuess(
                                "android.content.Intent"
                            )
                        )
                        paramMap?.forEach { (_, value) ->
                            val config = value.annoMap["@RouteParams"]
                            if (config != null){
                                val paramsName : String = config["name"] as String
                                val typeName = value.getTypeName()
                                typeName?.let {
                                    whatsMyName1.addParameter(paramsName,it)
                                    whatsMyName1.addStatement(
                                        "  intent.putExtra(\"$paramsName\",$paramsName)",
                                    )
                                }

                            }
                        }
                        whatsMyName1.addStatement(
                            "  context.startActivity(intent)",
                        )
                        whatsMyName1.addStatement(
                            "}"
                        )
                    }else{
                        classBuilder.addFunction(whatsMyName(classFunName)
                            .returns(classStar.copy(nullable = true))
                            .addStatement("return null")
                            .build())

                        whatsMyName1.addParameter("context",ClassName.bestGuess(
                            "android.content.Context"
                        ))
                        paramMap?.forEach { (_, value) ->
                            val config = value.annoMap["@RouteParams"]
                            if (config != null){
                                val paramsName : String = config["name"] as String
                                val typeName = value.getTypeName()
                                typeName?.let {
                                    whatsMyName1.addParameter(paramsName,it)
                                }

                            }
                        }
                    }

                    routeBuilder.addFunction(whatsMyName1.build())
                }else if (isSubtype(symbol,"androidx.fragment.app.Fragment") || isSubtype(symbol,"android.app.Fragment")){
                    val classFunName = "new${classKey}"
                    val anyClassName = ClassName.bestGuess(Any::class.qualifiedName!!)
                    val whatsMyName2 = whatsMyName("new$routeClassName")
                        .returns(anyClassName.copy(nullable = true))


                    if (!emptyRoute){
                        registerMapFun.addStatement("classMap[\"$usePath\"] = $pathInfoStr",*classMapTypeNames)
                        classBuilder.addFunction(whatsMyName(classFunName)
                            .returns(anyClassName.copy(nullable = true))
                            .addStatement("return %T()",ClassName.bestGuess(
                                className
                            ))
                            .build())

                        val activityBuilder = TypeSpec.classBuilder(
                            symbol.toString()
                        )

                        if (isSubtype(symbol,"androidx.fragment.app.Fragment")){
                            activityBuilder.superclass(ClassName.bestGuess("androidx.fragment.app.Fragment"))
                        }else{
                            activityBuilder.superclass(ClassName.bestGuess("android.app.Fragment"))
                        }

                        writeToFile(activityBuilder, symbol.packageName.asString(),symbol.toString(),symbol,true,false)
                        whatsMyName2.addStatement(
                            "val intent = %T()",
                            ClassName.bestGuess(
                                "android.content.Intent"
                            )
                        )
                        whatsMyName2.addStatement(
                            "val instance : %T = `$routeClassFile`(false).$classFunName()",
                            anyClassName.copy(nullable = true)
                        )
                        paramMap?.forEach { (_, value) ->
                            val config = value.annoMap["@RouteParams"]
                            if (config != null){
                                val paramsName : String = config["name"] as String
                                val typeName = value.getTypeName()
                                typeName?.let {
                                    whatsMyName2.addParameter(paramsName,it)
                                    whatsMyName2.addStatement(
                                        "intent.putExtra(\"$paramsName\",$paramsName)",
                                    )
                                }
                            }
                        }

                        if (isSubtype(symbol,"androidx.fragment.app.Fragment")){
                            whatsMyName2.addStatement(
                                "if (instance is %T) {"
                                ,ClassName.bestGuess(
                                    "androidx.fragment.app.Fragment"
                                )
                            )
                        }else{
                            whatsMyName2.addStatement(
                                "if (instance is %T) {"
                                ,ClassName.bestGuess(
                                    "android.app.Fragment"
                                )
                            )
                        }


                        whatsMyName2.addStatement(
                            "  instance.arguments = intent.getExtras()"
                        )

                        whatsMyName2.addStatement(
                            "}"
                        )

                        whatsMyName2.addStatement(
                            "return instance"
                        )
                    }else{
                        classBuilder.addFunction(whatsMyName(classFunName)
                            .returns(anyClassName.copy(nullable = true))
                            .addStatement("return null")
                            .build())

                        paramMap?.forEach { (_, value) ->
                            val config = value.annoMap["@RouteParams"]
                            if (config != null){
                                val paramsName : String = config["name"] as String
                                val typeName = value.getTypeName()
                                typeName?.let {
                                    whatsMyName2.addParameter(paramsName,it)
                                }
                            }
                        }
                        whatsMyName2.addStatement(
                            "return null"
                        )
                    }

                    routeBuilder.addFunction(whatsMyName2.build())
                }

                ksFiles.add(symbol.containingFile!!)
            }
            classBuilder.addFunction(registerMapFun.build())
            val getClazzFun = whatsMyName("getInfoByPath")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("path",String::class).returns(ClassName.bestGuess(PathInfo::class.qualifiedName!!).copy(nullable = true))
                .addStatement("return classMap[path]")

            val arrayClassName = ClassName.bestGuess(Array::class.qualifiedName!!)

            classBuilder.addFunction(getClazzFun.build())
            classBuilder.addFunction(whatsMyName("goByPath")
                .addParameter("path",String::class)
                .addParameter("params", mapClassName.parameterizedBy(ClassName.bestGuess(String::class.qualifiedName!!)
                    ,ClassName.bestGuess(Any::class.qualifiedName!!).copy(nullable = true)))
                .addParameter("byPath", Boolean::class)
                .addParameter("pathInfo", PathInfo::class)
                .addParameter("invokeRoute", Runnable::class)
                .addStatement("invokeRoute.run()")
                .addModifiers(KModifier.OVERRIDE)
                .addModifiers(KModifier.FINAL)
                .addModifiers(KModifier.PUBLIC)
                .build())
            writeToFile(classBuilder, routeModulePackageName,routeClassFile,true, ksFiles.toTypedArray())
            writeToFile(routeBuilder, routeModulePackageName,routeFile, true,ksFiles.toTypedArray())
        }
        return symbols.filter { !it.validate() }.toList()
    }

    private fun isSubtype(symbol: KSClassDeclaration,superType :String):Boolean{
        symbol.getAllSuperTypes().toList().forEach {
            val className = "${it.declaration.packageName.asString()}.${it}"
            if (className == superType){
                return true
            }
//            logger.error("symbol=${symbol},superTypes= ${it.declaration.packageName.asString()+"."+it}")
        }
        return false
    }

    private val routeParamsMap = mutableMapOf<String,MutableMap<String,RouteParamsConfig>>()
    private fun processRouteParams(resolver: Resolver): List<KSAnnotated> {
        val symbols =
            resolver.getSymbolsWithAnnotation(RouteParams::class.qualifiedName!!)
        for (symbol in symbols) {
            val annotationMap = getAnnotation(symbol)
            var className = "${(symbol as KSPropertyDeclaration).packageName.asString()}."
            var parent = symbol.parent
            while (parent !is KSFile){
                className = "$className$parent."
                parent = parent?.parent
            }
            className = className.substring(0,className.length-1)
            val classShortName = "${symbol.type.resolve().declaration}"
            val realClassName = "${symbol.type.resolve().declaration.packageName.asString()}.${classShortName}"
            val seeClassName = "${symbol.type.resolve().declaration.packageName.asString()}.${symbol.type}"
            val config = RouteParamsConfig(seeClassName,realClassName,symbol,annotationMap)
            var map = routeParamsMap[className]
            if (map == null){
                map = mutableMapOf()
                routeParamsMap[className] = map
            }
            val key = annotationMap["@RouteParams"].toString()
            map[key] = config
//            logger.error("annotationMap=$annotationMap")
//            logger.error("symbolLocation=$className${symbol}")
//            logger.error("symbolType=${symbol.type.resolve().declaration.packageName.asString()}.${symbol.type}")

        }
        return symbols.filter { !it.validate() }.toList()
    }

    private fun processExposeInterface(resolver: Resolver,implementSymbols: Sequence<KSAnnotated>): List<KSAnnotated> {
        val symbols =
            resolver.getSymbolsWithAnnotation(ExposeInterface::class.qualifiedName!!)
        for (symbol in symbols) {
            if (symbol is KSClassDeclaration) {
                val className = symbol.packageName.asString() + "." + symbol
                val isContain = isContainImplementClass(implementSymbols,className)

                if (!isContain){
                    throw IllegalArgumentException("注意: $className 没有相应的实现类")
                }

                val file = File((symbol.location as FileLocation).filePath)

                val fileName =
                    "${symbol}${file.absolutePath.substring(file.absolutePath.lastIndexOf("."))}"

                writeToFile(fileName, symbol, symbol.packageName.asString(), file)
            }

        }
        return symbols.filter { !it.validate() }.toList()
    }

    private fun isContainImplementClass(
        symbols: Sequence<KSAnnotated>,
        className: String
    ): Boolean {
//        logger.error("className = $className ,symbols= ${symbols}")
        var isContainImplementClass = false
        for (symbol in symbols) {
            val annotationMap = getAnnotation(symbol)
            val classMethodMap: MutableMap<String, Any?> =
                annotationMap["@ImplementClass"] ?: continue

            val value: KSType? =
                if (classMethodMap["value"] != null) classMethodMap["value"] as KSType else null
            val targetClassName: String? =
                (if (value != null) value.declaration.packageName.asString() + "." + value.toString() else null)
            if (targetClassName == className){
                if (symbol is KSClassDeclaration) {
                    val typeList = symbol.superTypes.toList()
                    for (ksTypeReference in typeList) {
                        val superClassName =
                            ksTypeReference.resolve().declaration.packageName.asString() + "." + ksTypeReference
                        if (superClassName == className) {
                            isContainImplementClass = true
                            break
                        }
                    }
                    if (!isContainImplementClass){
                        val thisName = symbol.packageName.asString() + "." + symbol
                        throw IllegalArgumentException("注意：实现类 $thisName，没有继承 $className")
                    }
                }
            }
        }
        return isContainImplementClass
    }

    private fun isImplementClass(
        symbol: KSAnnotated,
        className: String
    ): Boolean {
        var isImplementClass = false
        if (symbol is KSClassDeclaration) {
            val typeList = symbol.superTypes.toList()
            for (ksTypeReference in typeList) {
                val superClassName =
                    ksTypeReference.resolve().declaration.packageName.asString() + "." + ksTypeReference
                if (superClassName == className) {
                    isImplementClass = true
                    break
                }
            }
            if (!isImplementClass){
                val thisName = symbol.packageName.asString() + "." + symbol
                throw IllegalArgumentException("注意：实现类 $thisName 的 @ImplementClass 注解设置的类为$className，但却没有没有继承 $className")
            }
        }
        return isImplementClass
    }

    private fun getAnnotation(symbol: KSAnnotated): MutableMap<String, MutableMap<String, Any?>?> {
        val map = mutableMapOf<String, MutableMap<String, Any?>?>()
        for (annotation in symbol.annotations) {
            val annotationName = annotation.toString()
            var innerMap = map[annotationName]
            if (innerMap == null) {
                innerMap = mutableMapOf()
                map[annotationName] = innerMap
            }

            for (argument in annotation.arguments) {
                innerMap[argument.name?.getShortName().toString()] = argument.value as Any
            }
        }
        return map
    }


    private fun whatsMyName(name: String): FunSpec.Builder {
        return FunSpec.builder(name).addModifiers(KModifier.FINAL)
    }

    private fun writeToFile(
        fileName: String, symbol: KSAnnotated, packageName: String, file: File
    ) {
        FileInputStream(file).use { inputs ->
            val bytes = inputs.readAllBytes()
            codeGenerator
                .createNewFile(
                    Dependencies(false, symbol.containingFile!!),
                    packageName,
                    fileName,
                    "api"
                ).write(bytes)
        }

    }
    private fun writeToFile(
        typeBuilder: TypeSpec.Builder,
        packageName: String,
        fileName: String,
        symbol: KSAnnotated,
        writeApi:Boolean = false,
        writeKt:Boolean = true
    ) {
        val typeSpec = typeBuilder.build()
        val kotlinFile = FileSpec.builder(packageName, fileName).addType(typeSpec)
            .build()
        if (writeKt){
            codeGenerator
                .createNewFile(
                    Dependencies(false, symbol.containingFile!!),
                    packageName,
                    fileName
                )
                .writer()
                .use {
                    kotlinFile.writeTo(it)
                }
        }
        if (writeApi){

            codeGenerator
                .createNewFile(
                    Dependencies(false, symbol.containingFile!!),
                    packageName,
                    "$fileName.kt",
                    "api"
                )
                .writer()
                .use {
                    kotlinFile.writeTo(it)
                }
        }
    }

    private fun writeToFile(
        typeBuilder: TypeSpec.Builder,
        packageName: String,
        fileName: String,
        writeApi:Boolean = false,
        ksFiles : Array<KSFile>
    ) {
        val typeSpec = typeBuilder.build()
        val kotlinFile = FileSpec.builder(packageName, fileName).addType(typeSpec)
            .build()
        codeGenerator
            .createNewFile(
                Dependencies(false, *ksFiles),
                packageName,
                fileName
            )
            .writer()
            .use {
                kotlinFile.writeTo(it)
            }
        if (writeApi){

            codeGenerator
                .createNewFile(
                    Dependencies(false, *ksFiles),
                    packageName,
                    "$fileName.kt",
                    "api"
                )
                .writer()
                .use {
                    kotlinFile.writeTo(it)
                }
        }
    }
}