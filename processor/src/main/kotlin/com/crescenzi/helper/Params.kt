package com.crescenzi.helper

import com.crescenzi.Pref
import com.crescenzi.core.Values
import com.crescenzi.exception.throwError
import com.crescenzi.helper.shared.adjustCamelCase
import com.google.devtools.ksp.processing.KSPLogger

// == keys and default values == //
fun buildParams(logger: KSPLogger, prefList: MutableList<Pref<*>>): String {

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
        }  // == exception handled outside to avoid warning == //

        if(prefMethod.isEmpty()) throwError(logger,"Invalid Type")

        val def = when (pref.type) {
            String::class -> "\"${pref.defaultValue}\""
            else -> pref.defaultValue.toString()
        }

        // == if it is a float, the trailing f is appended == //
        val MUST_APPEND_F = if (pref.type == Float::class) "f" else ""



        sb.appendLine(
            """
    |@JvmStatic
    |val ${adjustCamelCase(pref.key)}${Values.KEY_SUFFIX} = $prefMethod("${pref.key}")
    |@JvmStatic
    |val ${adjustCamelCase(pref.key)}${Values.DEFAULT_SUFFIX}: ${pref.type.simpleName} = $def$MUST_APPEND_F
    |
    """.trimMargin()
        )

    }

    return sb.toString()
}

