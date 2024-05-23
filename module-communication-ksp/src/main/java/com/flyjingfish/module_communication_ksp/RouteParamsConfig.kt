package com.flyjingfish.module_communication_ksp

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import java.util.ArrayList

data class RouteParamsConfig(val className:String,val realClassName :String, val symbol: KSPropertyDeclaration, val annoMap : MutableMap<String, MutableMap<String, Any?>?>){

    fun getTypeName() : TypeName? {
        val config = annoMap["@RouteParams"] ?: return null
        val paramNullable: Boolean = config["nullable"] as Boolean
        return getTypeClazzName()?.typeName?.copy(nullable = paramNullable)
    }

    fun getTypeClazzName() : ParamTypeName? {
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
                val wholeTypeName = ClassName.bestGuess(this.realClassName)
                ParamTypeName(wholeTypeName,wholeTypeName,null)
            }
            Array::class.qualifiedName -> {
                val element = symbol.type.element
                if (element != null){
                    val typeArguments = element.typeArguments
                    val type = typeArguments[0].type
                    if (type != null){
                        val isSubtype = (type.resolve().declaration as KSClassDeclaration).isSubtype("java.io.Serializable")
                                ||(type.resolve().declaration as KSClassDeclaration).isSubtype("android.os.Parcelable")

                        val subPackageName = type.resolve().declaration.packageName.asString()
                        val subClazzName = type.resolve().declaration.toString()
                        val typeClazzName = "$subPackageName.$subClazzName"
                        val typeName = ClassName.bestGuess(this.realClassName)
                        if (isSubtype){
                            val wholeTypeName = typeName.parameterizedBy(ClassName.bestGuess(typeClazzName))
                            ParamTypeName(wholeTypeName,wholeTypeName,null)
                        }else{
                            throw IllegalArgumentException("$typeClazzName 至少需要实现 java.io.Serializable 或 android.os.Parcelable 这两个接口中的一个")
                        }
                    }else{
                        null
                    }
                }else{
                    null
                }

            }
            else ->{
                val isSubtype = (symbol.type.resolve().declaration as KSClassDeclaration).isSubtype("java.io.Serializable")
                        ||(symbol.type.resolve().declaration as KSClassDeclaration).isSubtype("android.os.Parcelable")

                if (className == realClassName){
                    if (isSubtype){
                        val wholeTypeName = ClassName.bestGuess(this.realClassName)
                        ParamTypeName(wholeTypeName,wholeTypeName,null)
                    }else{
                        throw IllegalArgumentException("$realClassName 至少需要实现 java.io.Serializable 或 android.os.Parcelable 这两个接口中的一个")
                    }
                }else{
                    if (realClassName == ArrayList::class.qualifiedName){
                        val element = symbol.type.element
                        if (element != null){
                            val typeArguments = element.typeArguments
                            val type = typeArguments[0].type
                            if (type != null){
                                val subPackageName = type.resolve().declaration.packageName.asString()
                                val subClazzName = type.resolve().declaration.toString()
                                val typeClazzName = "$subPackageName.$subClazzName"
                                val typeName = ClassName.bestGuess(this.realClassName)

                                val canUse = (type.resolve().declaration as KSClassDeclaration).isSubtype("android.os.Parcelable")
                                        || typeClazzName == Int::class.qualifiedName
                                        || typeClazzName == String::class.qualifiedName
                                        || typeClazzName == CharSequence::class.qualifiedName

                                if (canUse){
                                    val wholeTypeName = typeName.parameterizedBy(ClassName.bestGuess(typeClazzName))
                                    ParamTypeName(wholeTypeName,typeName,ClassName.bestGuess(typeClazzName))
                                }else{
                                    null
                                }
                            }else{
                                null
                            }
                        }else{
                            null
                        }
                    }else{
                        null
                    }
                }
            }
        }
    }
}