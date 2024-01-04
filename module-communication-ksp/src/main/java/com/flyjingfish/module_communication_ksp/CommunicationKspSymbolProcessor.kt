package com.flyjingfish.module_communication_ksp

import com.flyjingfish.module_communication_annotation.BindClass
import com.flyjingfish.module_communication_annotation.CommunicationPackage
import com.flyjingfish.module_communication_annotation.ExposeBean
import com.flyjingfish.module_communication_annotation.ExposeInterface
import com.flyjingfish.module_communication_annotation.ImplementClass
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.FileLocation
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import java.io.File
import java.io.FileInputStream


class CommunicationKspSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols =
            resolver.getSymbolsWithAnnotation(ImplementClass::class.qualifiedName!!)
        val ret1 = processImplementClass(symbols)
        val ret3 = processExposeInterface(resolver,symbols)
        val ret2 = processExposeBean(resolver)
        val ret = arrayListOf<KSAnnotated>()
        ret.addAll(ret1)
        ret.addAll(ret2)
        ret.addAll(ret3)
        return ret
    }

    private fun processImplementClass(symbols: Sequence<KSAnnotated>): List<KSAnnotated> {
        for (symbol in symbols) {
            val annotationMap = getAnnotation(symbol)
            val classMethodMap: MutableMap<String, Any?> =
                annotationMap["@ImplementClass"] ?: continue

            val value: KSType? =
                if (classMethodMap["clazz"] != null) classMethodMap["clazz"] as KSType else null
            val targetClassName: String =
                (if (value != null) value.declaration.packageName.asString() + "." + value.toString() else null)
                    ?: continue

            isImplementClass(symbol,targetClassName)

            val hoverboard = ClassName((symbol as KSClassDeclaration).packageName.asString(), "$symbol")
            val list = ClassName.bestGuess(BindClass::class.qualifiedName!!)
            val listOfHoverboards = list.parameterizedBy(hoverboard)
            val className = (symbol as KSClassDeclaration).packageName.asString() + "." + symbol

            val fileName = "${value.toString()}\$\$BindClass";
            val typeBuilder = TypeSpec.classBuilder(
                fileName
            ).addModifiers(KModifier.FINAL).addSuperinterface(listOfHoverboards)

            val whatsMyName1 = whatsMyName("getImplementClassInstance")
                .addModifiers(KModifier.OVERRIDE)
                .addModifiers(KModifier.FINAL)
                .addModifiers(KModifier.PUBLIC)
                .returns(ClassName.bestGuess(className))
                .addStatement("return $symbol()")

            typeBuilder.addFunction(whatsMyName1.build())
            writeToFile(typeBuilder, fileName, symbol)
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

    private fun processExposeInterface(resolver: Resolver,implementSymbols: Sequence<KSAnnotated>): List<KSAnnotated> {
        val symbols =
            resolver.getSymbolsWithAnnotation(ExposeInterface::class.qualifiedName!!)
        for (symbol in symbols) {
            if (symbol is KSClassDeclaration) {
                val className = symbol.packageName.asString() + "." + symbol
                val isContain = isContainImplementClass(implementSymbols,className)

                if (!isContain){
                    throw IllegalArgumentException("注意: $symbol 没有相应的实现类")
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
                if (classMethodMap["clazz"] != null) classMethodMap["clazz"] as KSType else null
            val targetClassName: String? =
                (if (value != null) value.declaration.packageName.asString() + "." + value.toString() else null)
            if (targetClassName == className){
                isContainImplementClass = true
            }
        }
        return isContainImplementClass
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
                throw IllegalArgumentException("注意：实现类 $symbol，没有继承 $className")
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
        typeBuilder: TypeSpec.Builder,
        fileName: String,
        symbol: KSAnnotated
    ) {
        val typeSpec = typeBuilder.build()
        val kotlinFile = FileSpec.builder(CommunicationPackage.BIND_CLASS_PACKAGE, fileName).addType(typeSpec)
            .build()
        codeGenerator
            .createNewFile(
                Dependencies(false, symbol.containingFile!!),
                CommunicationPackage.BIND_CLASS_PACKAGE,
                fileName
            )
            .writer()
            .use { kotlinFile.writeTo(it) }
    }
}