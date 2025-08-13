package code.helper

import code.Pref
import code.core.Values
import kotlin.collections.forEach


/**
 * Reset Method
 */
fun buildResetMethod(prefList: MutableList<Pref<*>>): String {

    val sb = StringBuilder()

    var pairs=""

    prefList.forEach { pref ->
        pairs+=("preferences[${pref.key.uppercase()}${Values.KEY_SUFFIX}] = ${pref.key.uppercase()}${Values.DEFAULT_SUFFIX} \n")
    }

    sb.append("""
            
        suspend fun resetAllPreferences() {

            prefs.edit { preferences->
               $pairs
            }        
        }
            
        """.trimIndent())

    return sb.toString()
}