package com.wire.kalium.cells.sdk.kmp

import com.wire.kalium.cells.sdk.kmp.api.NodeServiceApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class NodeServiceApiJsSmokeTest {

    @Test
    fun getByUuid_deserializes_rest_node_on_js() = runTest {
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
        val client = HttpClient(MockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
            engine {
                addHandler { request ->
                    assertEquals("Bearer test-token", request.headers[HttpHeaders.Authorization])
                    respond(
                        content = """
                            {
                              "Path": "/demo/file.txt",
                              "Uuid": "node-123",
                              "Type": "LEAF",
                              "PreSignedGET": {
                                "ExpiresAt": "2026-03-16T00:00:00Z",
                                "Url": "https://example.test/download"
                              }
                            }
                        """.trimIndent(),
                        status = HttpStatusCode.OK,
                        headers = io.ktor.http.headersOf(
                            HttpHeaders.ContentType,
                            ContentType.Application.Json.toString()
                        )
                    )
                }
            }
        }

        val api = NodeServiceApi(baseUrl = "https://cells.example.test", httpClient = client)
        api.setApiKey("test-token", "Authorization")
        api.setApiKeyPrefix("Bearer", "Authorization")

        val response = api.getByUuid(
            uuid = "node-123",
            flags = listOf(NodeServiceApi.FlagsGetByUuid.WithPreSignedURLs)
        )

        val body = response.body()
        assertEquals("/demo/file.txt", body.path)
        assertEquals("node-123", body.uuid)
        assertNotNull(body.preSignedGET)
        assertEquals("https://example.test/download", body.preSignedGET.url)
    }
}
