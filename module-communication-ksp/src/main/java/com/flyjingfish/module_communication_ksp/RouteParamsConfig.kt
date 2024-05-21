package com.flyjingfish.module_communication_ksp

import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName

data class RouteParamsConfig(val className:String,val realClassName :String, val symbol: KSPropertyDeclaration, val annoMap : MutableMap<String, MutableMap<String, Any?>?>){

    fun getTypeName() : TypeName? {
        val config = annoMap["@RouteParams"] ?: return null
        val paramNullable: Boolean = config["nullable"] as Boolean
        return getTypeClazzName()?.copy(nullable = paramNullable)
    }

    fun getTypeClazzName() : TypeName? {
        return when (this.realClassName) {
            Char::class.qualifiedName,CharArray::class.qualifiedName,
            Byte::class.qualifiedName,ByteArray::class.qualifiedName,
            Short::class.qualifiedName,ShortArray::class.qualifiedName,
            Int::class.qualifiedName,IntArray::class.qualifiedName,
            Long::class.qualifiedName,LongArray::class.qualifiedName,
            Float::class.qualifiedName,FloatArray::class.qualifiedName,
            Double::class.qualifiedName,DoubleArray::class.qualifiedName,
            Boolean::class.qualifiedName,BooleanArray::class.qualifiedName,
            String::class.qualifiedName -> {
                ClassName.bestGuess(this.realClassName)
            }
            Array::class.qualifiedName -> {
                val element = symbol.type.element
                if (element != null){
                    val typeArguments = element.typeArguments
                    val type = typeArguments[0].type
                    if (type != null){
                        val subPackageName = type.resolve().declaration.packageName.asString()
                        val subClazzName = type.resolve().declaration.toString()
                        val typeClazzName = "$subPackageName.$subClazzName"
                        val typeName = ClassName.bestGuess(this.realClassName)
                        typeName.parameterizedBy(ClassName.bestGuess(typeClazzName))
                    }else{
                        null
                    }
                }else{
                    null
                }

            }
            else ->{
                if (className == realClassName){
                    ClassName.bestGuess(this.realClassName)
                }else{
                    null
                }
            }
        }
    }
}