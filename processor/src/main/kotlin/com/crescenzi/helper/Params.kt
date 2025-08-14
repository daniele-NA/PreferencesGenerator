package com.crescenzi.helper

import com.crescenzi.Pref
import com.crescenzi.core.Values
import com.crescenzi.exception.throwError
import com.crescenzi.helper.shared.adjustCamelCase
import javax.annotation.processing.ProcessingEnvironment

/**
 * Chiavi e valori di default
 */
fun buildParams(processingEnv: ProcessingEnvironment, prefList: MutableList<Pref<*>>): String {

    val sb = StringBuilder()

    prefList.forEach { pref ->
        val prefMethod: String = when (pref.type) {
            String::class -> "stringPreferencesKey"
            Int::class -> "intPreferencesKey"
            Long::class -> "longPreferencesKey"
            Boolean::class -> "booleanPreferencesKey"
            Float::class -> "floatPreferencesKey"
            Double::class -> "doublePreferencesKey"
            else -> ""
        }  // Eccezione furoi per evitare Warning

        if(prefMethod.isEmpty()) throwError(processingEnv,"Invalid Type")

        val def = when (pref.type) {
            String::class -> "\"${pref.defaultValue}\""
            else -> pref.defaultValue.toString()
        }

        /**
         * Se è un float alla fine ci va la f
         */
        val MUST_APPEND_F = if (pref.type == Float::class) "f" else ""



        sb.appendLine(
            """
    |val ${adjustCamelCase(pref.key)}${Values.KEY_SUFFIX} = $prefMethod("${pref.key}")
    |val ${adjustCamelCase(pref.key)}${Values.DEFAULT_SUFFIX}: ${pref.type.simpleName} = $def$MUST_APPEND_F
    |
    """.trimMargin()
        )

    }

    return sb.toString()
}

