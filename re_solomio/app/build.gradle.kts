plugins {
    id("com.google.gms.google-services")
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.re_solomio"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.re_solomio"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // 부트 페이 결제 모듈 연동
    implementation("io.github.bootpay:android:+")
    implementation("io.github.bootpay:android-bio:+")

    // 파이어베이스 DB 연동
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage") // 이미지 연동을 위한 스토리지

    // Glide 및 Picasso 라이브러리
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
    implementation("com.google.android.gms:play-services-base:18.1.0") // AndroidX 및 Google Play Services 라이브러리
    implementation("com.google.firebase:firebase-core:21.0.0") // 파이어 베이스 코어
    implementation("com.squareup.picasso:picasso:2.71828") // 피카소 라이브러리
}

apply(plugin = "com.google.gms.google-services")