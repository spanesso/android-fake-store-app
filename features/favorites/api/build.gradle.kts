plugins {
    id("mango.android.library")
}

android {
    namespace = "com.mango.fakestore.features.favorites.api"
}

dependencies {
    api(project(":features:favorites:domain"))
}
