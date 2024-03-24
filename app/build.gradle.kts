import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.kotlinKsp)
    id("kotlin-parcelize")
//    alias(libs.plugins.jetbrainsParcel)
}

android {
    namespace = "com.app.news"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.app.news"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        val apikey: String = project.findProperty("API_KEY") as String
        val baseApi: String = project.findProperty("Base_Url") as String
        debug {
            buildConfigField("String", "BASE_API", baseApi)
            buildConfigField("String", "API_KEY", apikey)
//            buildConfigField("String","BASE_URL","\"https://newsapi.org/\"")
//            buildConfigField("String", "API_KEY", "\"79dce47c0e324f78b3817d8602c89703\"")
            isMinifyEnabled = false

        }
        release {
            buildConfigField("String", "BASE_API", baseApi)
            buildConfigField("String", "API_KEY", apikey)
//            buildConfigField("String", "BASE_URL", "\"https://newsapi.org/\"")
//            buildConfigField("String", "API_KEY", "\"79dce47c0e324f78b3817d8602c89703\"")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Room
//    implementation(libs.androidx.room.common)
    implementation(libs.androidx.roomRuntime)
    ksp(libs.androidx.roomCompiler)
    implementation(libs.androidx.roomKtx)


    // Live data and view model
    implementation(libs.androidx.livedate)
    implementation(libs.androidx.viewmodel)

    //coroutines
    implementation(libs.coroutine.android)
    implementation(libs.coroutine.core)

    //coil Image load
    implementation(libs.coil)

    // navigation
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.navigation.fragment)

    //retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.convert.gson)   // retrofit converter gson
    implementation(libs.retrofit.rxjava2) // rxjava2

    // okhttp3 Bom
    implementation(platform(libs.okhttp3.bom))
    implementation(libs.okhttp3) // okhttp3
    implementation(libs.okhttp3.logging) // okhttp3 logging interceptor

    // google Gson
    implementation(libs.gson)

    //hilt
    implementation(libs.googleHiltAndroid)
    ksp(libs.googleHiltCompiler)

//    implementation(libs.)

    implementation(libs.androidx.pagging)
    implementation(libs.androidx.pagging.ktx)


}