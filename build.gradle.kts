val ktorVersion = "2.0.3"
val mockkVersion = "1.12.3"
plugins {
    kotlin("multiplatform") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    id("maven-publish")
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
//        withJava()
    }
    js(IR) {
        browser()
        nodejs()
    }
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
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

                implementation("co.touchlab:kermit:1.1.3")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.ktor:ktor-client-mock:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-apache:$ktorVersion")
            }
        }
        val jvmTest by getting {
            dependencies {
                dependsOn(commonMain)
                implementation("io.mockk:mockk:1.12.3")
                implementation(kotlin("test"))
                implementation("org.junit.jupiter:junit-jupiter-params:5.6.0")

                implementation("org.junit.jupiter:junit-jupiter:5.8.1")
                implementation("org.testcontainers:testcontainers:1.16.3")
                implementation("org.testcontainers:junit-jupiter:1.16.3")
                implementation("org.testcontainers:postgresql:1.16.3")
                implementation("org.postgresql:postgresql:42.3.3")

                implementation("org.slf4j:slf4j-api:1.7.36")
                implementation("ch.qos.logback:logback-classic:1.2.11")


            }

        }
        val jsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-js:$ktorVersion")
            }
        }
        val jsTest by getting {
            dependencies {
            }
        }
        val iosMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-ios:$ktorVersion")
            }
        }
        val iosTest by getting {

        }
    }
}

tasks.named<Test>("jvmTest") {
    useJUnitPlatform()
//    testLogging {
//        showExceptions = true
//        showStandardStreams = true
//        events = setOf(org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED, org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED)
//        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
//    }
}

rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin> {
    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().nodeVersion = "16.14.2"
}