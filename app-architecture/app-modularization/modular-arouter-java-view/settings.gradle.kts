pluginManagement {
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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "modular-arouter-java-view"

// (1) 应用层
include(":app")

// (2) 业务组件层
include(":feature-home")
include(":feature-discover")
include(":feature-publish")
include(":feature-message")
include(":feature-profile")
include(":feature-detail")

// (3) 业务公共层
include(":common")

// (4) 基础层
include(":base")
