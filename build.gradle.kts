import java.util.*

plugins {
    kotlin("multiplatform") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
    id("maven-publish")
}

allprojects {
    version = "0.1.1-dev"
    group = "com.wire"
}

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    jvm()

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.3")

                api("io.ktor:ktor-client-core:2.3.13")
                api("io.ktor:ktor-client-serialization:2.3.13")
                api("io.ktor:ktor-client-content-negotiation:2.3.13")
                api("io.ktor:ktor-serialization-kotlinx-json:2.3.13")
                api("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
            }
        }

        commonTest.dependencies {
            implementation("org.jetbrains.kotlin:kotlin-test-common")
            implementation("io.ktor:ktor-client-okhttp:2.3.13")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
        }

        jvmMain {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("io.ktor:ktor-client-cio-jvm:2.3.13")

                // AWS SDK
                implementation(awssdk.services.s3)
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
    }
}

tasks.withType<Test> {
    val properties = Properties().apply {
        rootProject.file("local.properties").reader().use(::load)
    }
    val serverURL = properties["test.targer_server_url"] as String
    val pat = properties["test.targer_server_pat"] as String
    environment("TARGET_SERVER_URL", serverURL)
    environment("TARGET_SERVER_PAT", pat)
}
