plugins {
    id("com.android.library")
}

android {
    namespace = "com.example.modular.feature.publish"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        consumerProguardFiles("proguard-rules.pro")
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["AROUTER_MODULE_NAME"] = project.name
            }
        }
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
    resourcePrefix = "publish_"
}

dependencies {
    implementation(project(":base"))
    implementation(project(":common"))
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.alibaba:arouter-api:1.5.2")
    annotationProcessor("com.alibaba:arouter-compiler:1.5.2")
}
