plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.androidtwoctwopsandbox"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.androidtwoctwopsandbox"
        minSdk = 34
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("com.squareup.okhttp3:okhttp:4.2.1")
    implementation("com.beust:klaxon:5.5")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(fileTree(mapOf(
        "dir" to "./libs",
        "include" to listOf("*.aar", "*.jar")
    )))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("co.omise:omise-android:4.13.0")
    // required to avoid crash on Android 12 API 31
    implementation ("androidx.work:work-runtime-ktx:2.9.0")

    val fragmentVersion = "1.6.2"
    implementation("androidx.fragment:fragment-ktx:$fragmentVersion")

}