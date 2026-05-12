plugins {
    id("mango.android.library")
    id("mango.android.compose")
    alias(libs.plugins.paparazzi)
}

android {
    namespace = "com.mango.fakestore.core.designsystem"

    testOptions {
        unitTests {
            targetSdk = 35
        }
    }
}

// Los snapshot tests de Paparazzi se ejecutan separadamente con verifyPaparazziDebug.
// No se incluyen en el ciclo build estándar para evitar dependencias de entorno.
tasks.withType<Test>().configureEach {
    if (name.contains("UnitTest", ignoreCase = true)) {
        exclude("**/snapshot/**")
    }
}

dependencies {
    implementation(project(":core:error"))
    implementation(libs.coil.compose)

    testImplementation(libs.junit)
    testImplementation(libs.konsist)
    testImplementation(libs.mockk)
}
