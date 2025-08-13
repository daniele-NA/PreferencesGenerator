plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.kapt)
}


dependencies {
    implementation(libs.symbol.processing.api)
    implementation("com.google.auto.service:auto-service-annotations:1.0")
    kapt("com.google.auto.service:auto-service:1.0")


    /*
    Dipendenze android le ha il Client
     */
    api("androidx.datastore:datastore-preferences:1.1.0")
    api("androidx.datastore:datastore-preferences-core:1.1.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

}
