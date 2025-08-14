package com.crescenzi

import kotlin.reflect.KClass


@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class GeneratePreferences(val preferencesName: String)


class Pref<T>(internal val defaultValue: T, internal val key: String,internal val type: KClass<*>)

internal inline fun <reified T> prefFactory(defaultValue: T, key: String): Pref<T>{
    return Pref(defaultValue=defaultValue,key=key, type = T::class)
}


internal val allowedAnnotations = listOf(
    StringPref::class.java,
    IntPref::class.java,
    BooleanPref::class.java,
    LongPref::class.java,
    FloatPref::class.java,
    DoublePref::class.java
)


/*
“Applica l’annotazione StringPref al getter generato per questa proprietà.”
 */
@Target(AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.SOURCE)
annotation class StringPref(val defaultValue: String)

@Target(AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.SOURCE)
annotation class IntPref(val defaultValue: Int)

@Target(AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.SOURCE)
annotation class BooleanPref(val defaultValue: Boolean)

@Target(AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.SOURCE)
annotation class LongPref(val defaultValue: Long)

@Target(AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.SOURCE)
annotation class FloatPref(val defaultValue: Float)

@Target(AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.SOURCE)
annotation class DoublePref(val defaultValue: Double)


