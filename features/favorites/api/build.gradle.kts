plugins {
    id("mango.kotlin.library")
}

dependencies {
    implementation(project(":features:favorites:domain"))
}
