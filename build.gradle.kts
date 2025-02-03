import java.util.*
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.JavadocJar

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

mavenPublishing {
    // sources publishing is always enabled by the Kotlin Multiplatform plugin
    configure(KotlinMultiplatform(
        // configures the -javadoc artifact, possible values:
        // - `JavadocJar.None()` don't publish this artifact
        // - `JavadocJar.Empty()` publish an emprt jar
        // - `JavadocJar.Dokka("dokkaHtml")` when using Kotlin with Dokka, where `dokkaHtml` is the name of the Dokka task that should be used as input
        // javadocJar = JavadocJar.Dokka("dokkaHtml"),
        javadocJar = JavadocJar.Empty(),
        // whether to publish a sources jar
        sourcesJar = true,
        // configure which Android library variants to publish if this project has an Android target
        // defaults to "release" when using the main plugin and nothing for the base plugin
        androidVariantsToPublish = listOf("debug", "release"),
    ))
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

// Temporary disable signature to test published artifacts locally
//tasks.withType<Sign>().configureEach {
//    enabled = false
// }
// Allows skipping signing jars published to 'MavenLocal' repository
tasks.withType<Sign>().configureEach {
    if (System.getenv("CI") == null) { // i.e. not in Github Action runner
        enabled = false
    }
}

