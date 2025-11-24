package com.wire.kalium.cells.sdk.kmp.infrastructure

import kotlin.io.encoding.Base64

private val digits = "0123456789abcdef".toCharArray()

private fun String.toCharArray(): CharArray = CharArray(length) { get(it) }
internal fun ByteArray.encodeBase64(): String = Base64.encode(this)
internal fun String.decodeBase64Bytes(): ByteArray = Base64.decode(this)

/**
 * Encode [bytes] as a HEX string with no spaces, newlines and `0x` prefixes.
 *
 * Taken from https://github.com/ktorio/ktor/blob/master/ktor-utils/common/src/io/ktor/util/Crypto.kt
 */
fun hex(bytes: ByteArray): String {
    val result = CharArray(bytes.size * 2)
    var resultIndex = 0
    val digits = digits

    for (element in bytes) {
        val b = element.toInt() and 0xff
        result[resultIndex++] = digits[b shr 4]
        result[resultIndex++] = digits[b and 0x0f]
    }

    return result.concatToString()
}

/**
 * Decode bytes from HEX string. It should be no spaces and `0x` prefixes.
 *
 * Taken from https://github.com/ktorio/ktor/blob/master/ktor-utils/common/src/io/ktor/util/Crypto.kt
 */
internal fun hex(s: String): ByteArray {
    val result = ByteArray(s.length / 2)
    for (idx in result.indices) {
        val srcIdx = idx * 2
        val high = s[srcIdx].toString().toInt(16) shl 4
        val low = s[srcIdx + 1].toString().toInt(16)
        result[idx] = (high or low).toByte()
    }

    return result
}
