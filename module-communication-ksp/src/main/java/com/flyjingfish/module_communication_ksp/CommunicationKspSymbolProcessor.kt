package com.flyjingfish.module_communication_ksp

import com.flyjingfish.module_communication_annotation.BaseRouter
import com.flyjingfish.module_communication_annotation.BaseRouterClass
import com.flyjingfish.module_communication_annotation.BindClass
import com.flyjingfish.module_communication_annotation.ExposeBean
import com.flyjingfish.module_communication_annotation.ExposeInterface
import com.flyjingfish.module_communication_annotation.ImplementClass
import com.flyjingfish.module_communication_annotation.Route
import com.flyjingfish.module_communication_annotation.RouteParams
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
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
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
            val targetClassName: String =
                (value.declaration.packageName.asString() + "." + value.toString())

            isImplementClass(symbol,targetClassName)

            val implementClassName = ClassName((symbol as KSClassDeclaration).packageName.asString(), "$symbol")
            val bindClassName = ClassName.bestGuess(BindClass::class.qualifiedName!!)
            val superinterface = bindClassName.parameterizedBy(implementClassName)
            val className = symbol.packageName.asString() + "." + symbol

            val fileName = "${value.toString()}\$\$BindClass";
            val typeBuilder = TypeSpec.classBuilder(
                fileName
            ).addModifiers(KModifier.FINAL).addSuperinterface(superinterface)

            val whatsMyName1 = whatsMyName("getImplementClassInstance")
                .addModifiers(KModifier.OVERRIDE)
                .addModifiers(KModifier.FINAL)
                .addModifiers(KModifier.PUBLIC)
                .returns(ClassName.bestGuess(className))
                .addStatement("return $symbol()")

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
                    "${symbol}${file.absolutePath.substring(file.absolutePath.lastIndexOf("."))}";

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

            var fullName = ""
            val names = routeModuleName.split("-");
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

            val routeClassFile = "$moduleName\$\$RouterClass"
            val classBuilder = TypeSpec.objectBuilder(
                routeClassFile
            ).addSuperinterface(ClassName.bestGuess(BaseRouterClass::class.qualifiedName!!))
            val routeFile = "$moduleName\$\$Router"
            val routeBuilder = TypeSpec.objectBuilder(
                routeFile
            ).addSuperinterface(ClassName.bestGuess(BaseRouter::class.qualifiedName!!))

            val ksFiles = mutableListOf<KSFile>()
            for (symbol in symbols) {
                val annotationMap = getAnnotation(symbol)
                val className = (symbol as KSClassDeclaration).packageName.asString() + "." + symbol
                val path : String = annotationMap["@Route"]?.get("path") as String



                val classKey:String
                val routeClassName = if (path.isNotEmpty()){
                    classKey = path.replace(".","_").replace("/","_")
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                    classKey
                }else{
                    classKey = className.replace(".","_")
                    "$symbol"
                }


                val bindClassName = ClassName.bestGuess(Class::class.qualifiedName!!)
                val returnType = bindClassName.parameterizedBy(STAR)
//            val android = PropertySpec.builder(classKey, returnType)
//                .initializer("%T::class.java",  ClassName.bestGuess(
//                    className
//                ))
//                .build()
                val android = PropertySpec.builder(classKey, returnType.copy(nullable = true))
                    .mutable()
                    .initializer("null")
                    .addModifiers(KModifier.PRIVATE)
                    .build()
                classBuilder.addProperty(android)
                val classFunName = "get${classKey}Class"
                classBuilder.addFunction(whatsMyName(classFunName)
                    .returns(returnType)
                    .addStatement("val clazz = ${classKey}")
                    .addStatement("val returnClazz = if(clazz == null) {" )
                    .addStatement("  Class.forName(\"$className\")")
                    .addStatement("}else{" )
                    .addStatement("  clazz" )
                    .addStatement("}")
                    .addStatement("$classKey = clazz")
                    .addStatement("return returnClazz")
                    .build())

                val paramMap = routeParamsMap[className]

                if (isSubtype(symbol,"android.app.Activity")){
                    val whatsMyName1 = whatsMyName("go$routeClassName")
                        .addAnnotation(JvmStatic::class)
                    whatsMyName1.addParameter("context",ClassName.bestGuess(
                        "android.content.Context"
                    ))
                    whatsMyName1.addStatement(
                        "val intent = %T(context,`$routeClassFile`.$classFunName())",
                        ClassName.bestGuess(
                            "android.content.Intent"
                        )
                    )
                    paramMap?.forEach { (_, value) ->
                        val config = value.annoMap["@RouteParams"]
//                logger.error("paramMap-value=$value")
                        if (config != null){
                            val paramsName : String = config["name"] as String
//                    val paramsType : KSType = config["keyType"] as KSType
//                    val targetClassName: String = paramsType.declaration.packageName.asString() + "." + paramsType.toString()
                            val targetClassName: String = value.className
                            val bindClassName = ClassName.bestGuess(targetClassName)
                            whatsMyName1.addParameter(paramsName,bindClassName)
                            whatsMyName1.addStatement(
                                "intent.putExtra(\"$paramsName\",$paramsName)",
                            )
                        }
                    }
                    whatsMyName1.addStatement(
                        "context.startActivity(intent)",
                    )

                    routeBuilder.addFunction(whatsMyName1.build())
                }else if (isSubtype(symbol,"androidx.fragment.app.Fragment") || isSubtype(symbol,"android.app.Fragment")){
                    val whatsMyName2 = whatsMyName("newInstanceFor$routeClassName")
                        .addAnnotation(JvmStatic::class)
                        .returns(Any::class)

                    whatsMyName2.addStatement(
                        "val fragmentMeta : %T = `$routeClassFile`.$classFunName()",
                        returnType
                    )
                    whatsMyName2.addStatement(
                        "val intent = %T()",
                        ClassName.bestGuess(
                            "android.content.Intent"
                        )
                    )
                    whatsMyName2.addStatement(
                        "val instance = fragmentMeta.getConstructor().newInstance()",
                    )
                    paramMap?.forEach { (_, value) ->
                        val config = value.annoMap["@RouteParams"]
//                logger.error("paramMap-value=$value")
                        if (config != null){
                            val paramsName : String = config["name"] as String
//                    val paramsType : KSType = config["keyType"] as KSType
//                    val targetClassName: String = paramsType.declaration.packageName.asString() + "." + paramsType.toString()
                            val targetClassName: String = value.className
                            val bindClassName = ClassName.bestGuess(targetClassName)
                            whatsMyName2.addParameter(paramsName,bindClassName)
                            whatsMyName2.addStatement(
                                "intent.putExtra(\"$paramsName\",$paramsName)",
                            )
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

                    routeBuilder.addFunction(whatsMyName2.build())
                }

                ksFiles.add(symbol.containingFile!!)
            }
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
            val config = RouteParamsConfig("${symbol.type.resolve().declaration.packageName.asString()}.${symbol.type}",annotationMap)
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
                    "${symbol}${file.absolutePath.substring(file.absolutePath.lastIndexOf("."))}";

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
        writeApi:Boolean = false
    ) {
        val typeSpec = typeBuilder.build()
        val kotlinFile = FileSpec.builder(packageName, fileName).addType(typeSpec)
            .build()
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