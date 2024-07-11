package com.flyjingfish.module_communication_ksp

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.symbol.KSClassDeclaration

fun KSClassDeclaration.isSubtype(superType :String):Boolean{
    getAllSuperTypes().toList().forEach {
        val className = "${it.declaration.packageName.asString()}.${it.declaration}"
        if (className == superType){
            return true
        }
    }
    return false
}