// Módulo `:features:auth:data` — Submódulo data de feature.
// Aplica mango.android.library y mango.android.hilt para inyectar dependencias.
plugins {
    id("mango.android.library")
    id("mango.android.hilt")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.mango.fakestore.features.auth.data"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:error"))
    implementation(project(":core:datastore"))
    implementation(project(":core:database"))
    implementation(project(":features:auth:domain"))
    implementation(project(":features:profile:data"))
    implementation(project(":features:profile:domain"))

    implementation(libs.arrow.core)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlinx.serialization)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.truth)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(project(":core:testing"))
}
