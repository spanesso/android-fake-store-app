plugins {
    id("mango.android.library")
    id("mango.android.hilt")
}

android {
    namespace = "com.mango.fakestore.core.analytics"
}

dependencies {
    implementation(project(":core:error"))
    implementation(project(":core:common"))

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.performance)
    implementation(libs.timber)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.truth)
    testImplementation(libs.kotlinx.coroutines.test)
}
