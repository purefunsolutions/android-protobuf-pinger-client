import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.protobuf")
}

android {
    buildToolsVersion = "33.0.0"
    compileSdk = 32

    defaultConfig {
        applicationId = "fi.purefun.androidprotobufpinger"
        minSdk = 30
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {
                cppFlags("")
            }
        }
    }

    buildTypes {
        release {
            // minifyEnabled = false
            // proguardFiles = (getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.0"
    }

    sourceSets["main"].java.srcDir("src/main/java")
    sourceSets["main"].resources.srcDir("src/main/proto")
}

protobuf {
    protoc {
        path = "/nix/store/yrsxw3lw3csd94z4h5lm2qd3v3i6pk9m-protobuf-3.19.4/bin/protoc"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.46.0"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.3.0:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("java")
                id("grpc")
                id("grpckt")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}

dependencies {
    implementation("androidx.activity:activity-compose:1.5.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("androidx.compose.foundation:foundation-layout:1.2.1")
    implementation("androidx.compose.material:material:1.2.1")
    implementation("androidx.compose.runtime:runtime:1.2.1")
    implementation("androidx.compose.ui:ui:1.2.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("com.google.android.material:material:1.6.1")
    implementation("com.google.protobuf:protobuf-kotlin:3.21.5")
    runtimeOnly("io.grpc:grpc-okhttp:1.46.0")
    api("io.grpc:grpc-protobuf:1.46.0")
    api("io.grpc:grpc-stub:1.46.0")
    api("io.grpc:grpc-kotlin-stub:1.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.2")
    implementation(kotlin("stdlib"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")


}
