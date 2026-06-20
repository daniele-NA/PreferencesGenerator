package com.crescenzi.helper

import com.crescenzi.Pref
import com.crescenzi.core.Values
import com.crescenzi.helper.shared.adjustCamelCase


// == reset method == //
fun buildResetMethod(prefList: MutableList<Pref<*>>): String {

    val sb = StringBuilder()

    val pairs = prefList.joinToString("\n") { pref ->
        val key = adjustCamelCase(pref.key) + Values.KEY_SUFFIX   // == PRIMARY_COLOR_KEY == //
        val default = adjustCamelCase(pref.key) + Values.DEFAULT_SUFFIX // == PRIMARY_COLOR_DEFAULT == //
        "                preferences[$key] = $default"  // == spaces for indentation inside prefs.edit == //
    }

    sb.append(
        """
        fun resetAllPreferences() {

        scope.launch{
                prefs.edit { preferences ->
$pairs
                }  
            }
        }
        """.trimIndent()
    )

    return sb.toString()
}
