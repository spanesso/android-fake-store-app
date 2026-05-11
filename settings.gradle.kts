pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "fakestoreapp"

// Módulo principal de ensamblaje (ya existente desde el scaffold inicial).
include(":app")

// Módulos transversales (:core:*) — estructura vacía generada en ETAPA 0.5; la
// implementación llega en ETAPA 1 (núcleo) siguiendo §4 del prompt maestro.
include(":core:analytics")
include(":core:common")
include(":core:database")
include(":core:datastore")
include(":core:design-system")
include(":core:error")
include(":core:network")
include(":core:security")
include(":core:testing")
include(":core:ui")

// Módulos de features (:features:*:{api,domain,data,presentation}) — estructura vacía
// generada en ETAPA 0.5. Cada feature se completa en su propia ETAPA (2 auth, 3 products,
// 4 favorites, 5 profile).
include(":features:auth:api")
include(":features:auth:data")
include(":features:auth:domain")
include(":features:auth:presentation")
include(":features:favorites:api")
include(":features:favorites:data")
include(":features:favorites:domain")
include(":features:favorites:presentation")
include(":features:products:api")
include(":features:products:data")
include(":features:products:domain")
include(":features:products:presentation")
include(":features:profile:api")
include(":features:profile:data")
include(":features:profile:domain")
include(":features:profile:presentation")
