//add read .env file content method - IM/2021/089 -- start
import java.io.File
import java.util.Properties

fun getEnvValue(key: String): String {
    val properties = Properties()
    val envFile = File(".env")
    if (envFile.exists()) {
        properties.load(envFile.inputStream())
    }
    return properties.getProperty(key, "")
}
//add read .env file content method - IM/2021/089 -- stop

plugins {
    // add plugins for firbase - IM/2021/089 --start
    id("com.android.application")
    id("com.google.gms.google-services")
    // add plugins for firbase - IM/2021/089 --stop
}

android {
    namespace = "com.example.mobileapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mobileapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "DATABASE_URL", "\"${getEnvValue("DATABASE_URL")}\"")
    }

    // Enable buildConfig feature for support custom featurs from .env file - IM/2021/089 --start
    buildFeatures {
        buildConfig = true
    }
    // Enable buildConfig feature for support custom featurs from .env file - IM/2021/089 --stop

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures{
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // add dependencies for firbase - IM/2021/089 --start
    implementation(platform("com.google.firebase:firebase-bom:33.2.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database")

    // Declare the dependency for the Cloud Firestore library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-firestore")
    implementation ("com.google.firebase:firebase-auth")
    implementation ("com.google.firebase:firebase-storage:20.2.0")
    // add dependencies for firbase - IM/2021/089 --stop
}