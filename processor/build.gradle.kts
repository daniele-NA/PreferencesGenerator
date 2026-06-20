plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
}


// == JVM-MODULE == //
dependencies {
    implementation(libs.symbol.processing.api)

    api("androidx.datastore:datastore-preferences:1.1.0")
    api("androidx.datastore:datastore-preferences-core:1.1.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

}
