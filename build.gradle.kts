import java.util.*

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)

    id(libs.plugins.vanniktech.publish.get().pluginId) version libs.versions.vanniktech.publish
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
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.core)

                api(libs.ktor.client.core)
                api(libs.ktor.client.serialization)
                api(libs.ktor.client.content.negotiation)
                api(libs.ktor.serialization.kotlinx.json)
                api(libs.kotlinx.datetime)
            }
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test.common)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.coroutines.test)
        }

        jvmMain {
            dependencies {
                implementation(libs.kotlin.stdlib.jdk8)
                implementation(libs.ktor.client.cio.jvm)

                // AWS SDK
                implementation(awssdk.services.s3)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.kotlin.test.junit)
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
