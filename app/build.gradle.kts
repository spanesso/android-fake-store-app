plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    // firebase-perf plugin (1.4.2) uses the removed AGP Transform API and is incompatible with AGP 9+.
    // Custom traces via FirebasePerformance SDK are still fully functional.
    alias(libs.plugins.gradle.play.publisher)
}

android {
    namespace = "com.example.fakestoreapp"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    signingConfigs {
        create("release") {
            val keystorePath = System.getenv("KEYSTORE_PATH") ?: ""
            if (keystorePath.isNotEmpty()) {
                storeFile = file(keystorePath)
                storePassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
                keyAlias = System.getenv("KEY_ALIAS") ?: ""
                keyPassword = System.getenv("KEY_PASSWORD") ?: ""
            }
        }
    }

    defaultConfig {
        applicationId = "com.example.fakestoreapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions += "env"
    productFlavors {
        create("dev") {
            dimension = "env"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            buildConfigField("String", "INTEGRITY_POLICY", "\"LOG\"")
            buildConfigField("String", "EXPECTED_CERT_HASH", "\"\"")
        }
        create("staging") {
            dimension = "env"
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            buildConfigField("String", "INTEGRITY_POLICY", "\"WARN\"")
            buildConfigField("String", "EXPECTED_CERT_HASH", "\"\"")
        }
        create("prod") {
            dimension = "env"
            buildConfigField("String", "INTEGRITY_POLICY", "\"BLOCK\"")
            val certHash = project.findProperty("RELEASE_CERT_HASH")?.toString() ?: ""
            buildConfigField("String", "EXPECTED_CERT_HASH", "\"$certHash\"")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

ksp {
    arg("dagger.hilt.android.internal.disableAndroidSuperclassValidation", "true")
    arg("dagger.hilt.android.internal.projectType", "APP")
}

play {
    track.set("internal")
    defaultToAppBundles.set(true)
    serviceAccountCredentials.set(
        file(System.getenv("ANDROID_PUBLISHER_CREDENTIALS_FILE") ?: "non-existent-credentials.json")
    )
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(project(":core:design-system"))
    implementation(project(":core:ui"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:common"))
    implementation(project(":core:error"))
    implementation(project(":core:analytics"))
    implementation(project(":core:logging"))
    implementation(libs.androidx.datastore.preferences)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.performance)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp.core)
    implementation(libs.retrofit.core)
    // Hilt wiring — el módulo :app ensambla el grafo DI completo (ARQ-010 exception)
    // data: contiene los @Module Hilt que ligan interfaces domain → implementaciones Room/Retrofit
    // domain: transitivo necesario para que ksp(hilt.compiler) resuelva bindings correctamente
    implementation(project(":features:auth:api"))
    implementation(project(":features:auth:domain"))
    implementation(project(":features:auth:data"))
    implementation(project(":features:auth:presentation"))
    implementation(project(":features:products:domain"))
    implementation(project(":features:products:data"))
    implementation(project(":features:products:presentation"))
    implementation(project(":features:favorites:api"))
    implementation(project(":features:favorites:data"))
    implementation(project(":features:favorites:presentation"))
    implementation(project(":features:profile:domain"))
    implementation(project(":features:profile:data"))
    implementation(project(":features:profile:presentation"))
    implementation(project(":core:security"))
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(project(":core:testing"))
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.hilt.android)
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.52")
    kspAndroidTest(libs.hilt.compiler)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
