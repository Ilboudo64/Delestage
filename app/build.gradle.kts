import com.android.build.gradle.ProguardFiles.getDefaultProguardFile
import org.apache.commons.logging.LogFactory.release
import org.gradle.internal.impldep.com.amazonaws.PredefinedClientConfigurations.defaultConfig

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}
android {
    namespace = "com.example.delestage"
    // On utilise la syntaxe standard ici
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.delestage"
        minSdk = 24
        targetSdk = 35 // Le SDK 36 est encore très récent/beta, 35 est plus stable
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    // Bibliothèques de base
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)

    // Firebase et Services
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-database:20.3.0")

    // Localisation et Cartes
    implementation("com.google.android.gms:play-services-location:21.1.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("org.osmdroid:osmdroid-android:6.1.18")
    implementation(libs.support.compat)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}