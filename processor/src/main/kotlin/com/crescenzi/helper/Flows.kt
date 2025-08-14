package com.crescenzi.helper

import com.crescenzi.Pref
import com.crescenzi.core.Values
import com.crescenzi.helper.shared.adjustCamelCase
import kotlin.collections.forEach

/*/
Flows Fields
 */
fun buildFlows(prefList : MutableList<Pref<*>> ): String{

    val sb = StringBuilder()



    prefList.forEach { pref ->

        val key=adjustCamelCase(pref.key)+Values.KEY_SUFFIX   // PRIMARY_COLOR_KEY
        val default=adjustCamelCase(pref.key)+Values.DEFAULT_SUFFIX // PRIMARY_COLOR_DEFAULT

        sb.appendLine(
            """
                            
val ${pref.key}: StateFlow<${pref.type.simpleName}> = prefs.data
    .map { preferences -> preferences[$key] ?: $default }
    .stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = $default 
    )         
           """.trimIndent()
        )
    }

    return sb.toString()

}