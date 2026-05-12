// Módulo `:core:testing` — Android library con utilidades de test compartidas.
// Se incluye como testImplementation(project(":core:testing")) en otros módulos.
plugins {
    id("mango.android.library")
}

android {
    namespace = "com.mango.fakestore.core.testing"
}

dependencies {
    implementation(project(":core:error"))
    implementation(project(":core:common"))

    implementation(libs.junit)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.mockk)
    implementation(libs.truth)
    implementation(libs.turbine)
    implementation(libs.arrow.core)
}
