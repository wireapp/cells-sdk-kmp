import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.maven.publish)
}

allprojects {
    version = (project.findProperty("VERSION_NAME") as? String) ?: "0.1.1-dev"
    group = "com.wire"
}

repositories {
    mavenLocal()
    mavenCentral()
    google()
}

kotlin {
    jvm()

    iosArm64()
    iosSimulatorArm64()
    macosArm64()

    // These two can be removed Soon™, as they are only required if you want to support Intel-based Macs,
    // which most projects have already dropped support for.
    iosX64()
    macosX64()

    sourceSets {
        commonMain {
            dependencies {
                api(libs.ktor.client.core)
                api(libs.ktor.client.serialization)
                api(libs.ktor.client.content.negotiation)
                api(libs.ktor.serialization.kotlinx.json)
                api(libs.kotlinx.datetime)

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.core)
            }
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test.common)
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
                implementation(libs.ktor.client.okhttp)
                implementation(libs.kotlin.test.junit)
            }
        }

        appleMain {
            /* dependsOn(commonMain.get()) */
            dependencies {
                implementation(libs.ktor.iosHttp)
            }
        }

        appleTest {
            dependencies {
                implementation(libs.ktor.iosHttp)
            }
        }
    }
}

// We only sign artifacts when explicitly required
tasks.withType<Sign>().configureEach {
    if (System.getenv("SIGN_ARTIFACTS") != "true") { // e.g. not in Github Action runner
        enabled = false
    }
}

// More info in the manifest for JVM jars
tasks.withType<Jar>().configureEach {
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "Wire Swiss GmbH",
            "Built-JDK" to System.getProperty("java.version")
        )
    }
}

tasks.withType<Test> {
    val properties = Properties().apply {
        rootProject.file("local.properties").reader().use(::load)
    }
    val serverURL = properties["test.target_server_url"] ?: ""
    val pat = properties["test.target_server_pat"] ?: ""
    environment("TARGET_SERVER_URL", serverURL)
    environment("TARGET_SERVER_PAT", pat)
}

// Useless for now
//mavenPublishing {
//    // sources publishing is always enabled by the Kotlin Multiplatform plugin
//    configure(KotlinMultiplatform(
//        // configures the -javadoc artifact, possible values:
//        // - `JavadocJar.None()` don't publish this artifact
//        // - `JavadocJar.Empty()` publish an empty jar
//        // - `JavadocJar.Dokka("dokkaHtml")` when using Kotlin with Dokka, where `dokkaHtml` is the name of the Dokka task that should be used as input
//        // javadocJar = JavadocJar.Dokka("dokkaHtml"),
//        javadocJar = JavadocJar.Empty(),
//        // whether to publish a sources jar
//        sourcesJar = true,
//        // configure which Android library variants to publish if this project has an Android target
//        // defaults to "release" when using the main plugin and nothing for the base plugin
//        androidVariantsToPublish = listOf("debug", "release"),
//    ))
//}
