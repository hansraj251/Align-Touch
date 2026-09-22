plugins {
    id("com.android.application")

    id("org.jetbrains.kotlin.android")

    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.chatflow.app"

    compileSdk = 36

    defaultConfig {
        applicationId = "com.chatflow.app"

        minSdk = 26

        targetSdk = 37

        versionCode = 1

        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility =
            JavaVersion.VERSION_17

        targetCompatibility =
            JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(
        platform(
            "androidx.compose:compose-bom:2026.06.01"
        )
    )

    implementation(
        "androidx.compose.ui:ui"
    )

    implementation(
        "androidx.compose.ui:ui-tooling-preview"
    )

    implementation(
        "androidx.compose.material3:material3"
    )

    implementation(
        "androidx.compose.material:material-icons-extended"
    )

    implementation(
        "androidx.activity:activity-compose:1.12.0"
    )

    implementation(
        "androidx.lifecycle:lifecycle-runtime-compose:2.10.0"
    )
    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0"
    )

    debugImplementation(
        "androidx.compose.ui:ui-tooling"
    )

    implementation(
        "com.squareup.retrofit2:retrofit:3.0.0"
    )

    implementation(
        "com.squareup.retrofit2:converter-gson:3.0.0"
    )

    implementation(
        "com.squareup.okhttp3:logging-interceptor:5.1.0"
    )

    implementation(
        "io.socket:socket.io-client:2.1.0"
    )
}
