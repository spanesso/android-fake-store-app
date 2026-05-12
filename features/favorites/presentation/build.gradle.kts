plugins {
    id("mango.android.feature")
}

android {
    namespace = "com.mango.fakestore.features.favorites.presentation"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:error"))
    implementation(project(":core:design-system"))
    implementation(project(":core:ui"))
    implementation(project(":core:analytics"))
    implementation(project(":features:favorites:domain"))

    implementation(libs.arrow.core)
    implementation(libs.coil.compose)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.hilt.navigation.compose)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.truth)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(project(":core:testing"))
}
