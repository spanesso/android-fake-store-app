plugins {
    id("mango.android.library")
    id("mango.android.hilt")
}

android {
    namespace = "com.mango.fakestore.core.datastore"
}

dependencies {
    implementation(project(":core:common"))

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.google.tink.android)
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.timber)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.truth)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
}
