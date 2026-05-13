// Módulo `:features:auth:presentation` — Submódulo presentation de feature.
// Aplica mango.android.feature (library + compose + hilt).
plugins {
    id("mango.android.feature")
}

android {
    namespace = "com.mango.fakestore.features.auth.presentation"
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:error"))
    implementation(project(":core:design-system"))
    implementation(project(":core:ui"))
    implementation(project(":core:analytics"))
    implementation(project(":features:auth:domain"))
    implementation(project(":features:auth:api"))

    implementation(libs.arrow.core)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.hilt.navigation.compose)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.truth)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(project(":core:testing"))
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.compose.ui.test.junit4)
}
