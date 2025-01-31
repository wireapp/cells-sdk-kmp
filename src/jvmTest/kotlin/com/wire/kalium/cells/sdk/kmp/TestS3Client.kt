package com.wire.kalium.cells.sdk.kmp

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import aws.smithy.kotlin.runtime.collections.Attributes
import aws.smithy.kotlin.runtime.collections.emptyAttributes
import aws.smithy.kotlin.runtime.content.asByteStream
import aws.smithy.kotlin.runtime.net.url.Url
import aws.smithy.kotlin.runtime.time.Instant
import okhttp3.OkHttpClient
import java.io.File
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.io.path.createTempFile
import kotlin.io.path.writeText

const val DEFAULT_GATEWAY_SECRET = "gatewaysecret"
const val DEFAULT_S3_REGION_NAME = "us-east-1"
const val DEFAULT_BUCKET_NAME = "io"

suspend fun putS3Object( serverUrl: String, pat: String, objectKey: String) {
    val metadataVal = mutableMapOf<String, String>()
    // metadataVal["myVal"] = "test"
    val creds = PatCredentials(pat, DEFAULT_GATEWAY_SECRET)

  val request = PutObjectRequest {
        bucket = DEFAULT_BUCKET_NAME
        key = objectKey
        metadata = metadataVal
        body = createDummyTmpFile().asByteStream()
    }

    S3Client {
        region = DEFAULT_S3_REGION_NAME
        credentialsProvider = StaticCredentialsProvider(creds)
        endpointUrl = Url.parse(serverUrl)
    }
        .use { s3 ->
            val response = s3.putObject(request)
            println("Tag information is ${response.eTag}")
        }
}

private class PatCredentials(
    override val accessKeyId: String,
    override val secretAccessKey: String,
    override val sessionToken: String? = null,
    override val expiration: Instant? = null,
    override val attributes: Attributes = emptyAttributes(),
) : Credentials

private fun createDummyTmpFile(): File {
    val path = createTempFile(prefix = "temp_", suffix = ".txt")
    path.writeText("This is a test file to test put object with an unique part: ${unique(8)}")
    return path.toFile()
}

private fun unsafeClientBuilder(): OkHttpClient.Builder {
    try {
        // Create a trust manager that does not validate certificate chains
        val trustAllCerts =
            arrayOf<TrustManager>(
                object : X509TrustManager {
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                })

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        val sslSocketFactory = sslContext.socketFactory

        // Create an OkHttpClient and configure it to ignore certificate errors
        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier(HostnameVerifier { _, _ -> true })
    } catch (e: Exception) {
        throw RuntimeException(e)
    }
}
