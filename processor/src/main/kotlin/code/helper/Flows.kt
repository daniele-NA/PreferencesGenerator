package code.helper

import code.Pref
import code.core.Values
import kotlin.collections.forEach

/*/
Flows Fields
 */
fun buildFlows(prefList : MutableList<Pref<*>> ): String{

    val sb = StringBuilder()



    prefList.forEach { pref ->

        val defFieldName=pref.key.uppercase()+Values.DEFAULT_SUFFIX

        sb.appendLine(
            """
                            
val ${pref.key}: StateFlow<${pref.type.simpleName}> = prefs.data
.map { preferences -> preferences[${pref.key.uppercase()}${Values.KEY_SUFFIX}] ?: $defFieldName }
.stateIn(
    scope = scope,
    started = SharingStarted.Eagerly,
    initialValue = $defFieldName )   
                   
           """.trimIndent()
        )
    }

    return sb.toString()

}