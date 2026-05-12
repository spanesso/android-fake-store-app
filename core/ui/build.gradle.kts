plugins {
    id("mango.android.library")
    id("mango.android.compose")
}

android {
    namespace = "com.mango.fakestore.core.ui"
}

dependencies {
    implementation(project(":core:design-system"))
    implementation(project(":core:error"))
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.runtime.compose)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}
