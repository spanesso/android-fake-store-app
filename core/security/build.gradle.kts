plugins {
    id("mango.android.library")
    id("mango.android.hilt")
    id("mango.android.compose")
}

android {
    namespace = "com.mango.fakestore.core.security"
}

dependencies {
    implementation(project(":core:error"))
    implementation(project(":core:common"))

    implementation(libs.androidx.biometric)
    implementation(libs.rootbeer)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.truth)
    testImplementation(libs.kotlinx.coroutines.test)
}
