package com.crescenzi.helper.shared

import java.util.Locale

/**
 * danielSun -> DANIEL_SUN
 */

internal fun adjustCamelCase(name: String): String {
    val snake = name.replace(Regex("([a-z])([A-Z])"), "$1_$2").uppercase(Locale.getDefault())
    return snake
}
