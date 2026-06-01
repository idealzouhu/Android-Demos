plugins {
    id("com.android.application")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.modular"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.modular.arouter.demo"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":base"))
    implementation(project(":common"))

    if (!(rootProject.findProperty("isFeatureHomeDebug")?.toString()?.toBoolean() == true)) {
        implementation(project(":feature-home"))
    }
    if (!(rootProject.findProperty("isFeatureDiscoverDebug")?.toString()?.toBoolean() == true)) {
        implementation(project(":feature-discover"))
    }
    if (!(rootProject.findProperty("isFeaturePublishDebug")?.toString()?.toBoolean() == true)) {
        implementation(project(":feature-publish"))
    }
    if (!(rootProject.findProperty("isFeatureMessageDebug")?.toString()?.toBoolean() == true)) {
        implementation(project(":feature-message"))
    }
    if (!(rootProject.findProperty("isFeatureProfileDebug")?.toString()?.toBoolean() == true)) {
        implementation(project(":feature-profile"))
    }
    if (!(rootProject.findProperty("isFeatureDetailDebug")?.toString()?.toBoolean() == true)) {
        implementation(project(":feature-detail"))
    }

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.alibaba:arouter-api:1.5.2")
    implementation("com.google.dagger:hilt-android:2.51.1")
    annotationProcessor("com.google.dagger:hilt-compiler:2.51.1")
}
