plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "sv.edu.udb.exploramundodesafio"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "sv.edu.udb.exploramundodesafio"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Dependencias originales de Android
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)

    // Listas y ciclo de vida
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Consumo de las tres APIs REST
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Corrutinas para operaciones asíncronas
    implementation(libs.kotlinx.coroutines.android)

    // Carga de banderas e iconos meteorológicos
    implementation(libs.glide)

    // Mapa interactivo
    implementation(libs.osmdroid.android)

    // Pruebas
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
