val ktorVersion = "1.6.8"
val mockkVersion = "1.12.3"
plugins {
    kotlin("multiplatform") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("maven-publish")
    id("com.google.devtools.ksp") version "1.6.10-1.0.4"
}

group = "io.supabase.postgrest"
version = "0.3.0"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
    }
//    js(IR) {
//        browser()
//    }
    val iosTarget: (String, org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget.() -> Unit) -> org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget =
        when {
            System.getenv("SDK_NAME")?.startsWith("iphoneos") == true -> ::iosArm64
            System.getenv("NATIVE_ARCH")?.startsWith("arm") == true -> ::iosSimulatorArm64
            else -> ::iosX64
        }

    iosTarget("ios") {
        binaries {
            framework {
                baseName = "shared"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project.dependencies.platform("org.jetbrains.kotlin:kotlin-bom"))
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.mockative:mockative:1.1.4")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-apache:$ktorVersion")
            }
        }
        val jvmTest by getting {
            dependencies {
            }

            kotlin.srcDir("build/generated/ksp/jvm/jvmTest/kotlin")
        }
//        val jsMain by getting {
//            dependencies {
//                implementation("io.ktor:ktor-client-js:$ktorVersion")
//            }
//        }
//        val jsTest by getting {
//            dependencies {
//            }
//        }
        val iosMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-ios:$ktorVersion")
            }
        }
        val iosTest by getting {
            kotlin.srcDir("build/generated/ksp/ios/iosTest/kotlin")
        }
    }
}

rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin> {
    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().nodeVersion = "16.14.2"
}

ksp {
    arg("mockative.logging", "debug")
}

dependencies {
    ksp("io.mockative:mockative-processor:1.1.4")
//    add("kspMetadata","io.mockative:mockative-processor:1.1.4")
//    add("kspJvmTest","io.mockative:mockative-processor:1.1.4")
//    add("kspJvmMain","io.mockative:mockative-processor:1.1.4")
//    add("kspIosArm64Test","io.mockative:mockative-processor:1.1.4")
//    add("kspIosArm64Main","io.mockative:mockative-processor:1.1.4")
//    add("kspIosX64Test","io.mockative:mockative-processor:1.1.4")
//    add("kspIosX64Main","io.mockative:mockative-processor:1.1.4")
}