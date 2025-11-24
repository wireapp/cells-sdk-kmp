package com.wire.kalium.cells.sdk.kmp.infrastructure

import io.ktor.util.decodeBase64Bytes
import io.ktor.util.encodeBase64
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeFully
import io.ktor.utils.io.core.writeText
import kotlinx.io.readByteArray
import kotlin.experimental.and

private val digits = "0123456789abcdef".toCharArray()
private const val BASE64_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
private const val BASE64_MASK: Byte = 0x3f
private const val BASE64_PAD = '='
private val BASE64_INVERSE_ALPHABET = IntArray(256) { BASE64_ALPHABET.indexOf(it.toChar()) }

private fun String.toCharArray(): CharArray = CharArray(length) { get(it) }
private fun ByteArray.clearFrom(from: Int) = (from until size).forEach { this[it] = 0 }
private fun Int.toBase64(): Char = BASE64_ALPHABET[this]
private fun Byte.fromBase64(): Byte = BASE64_INVERSE_ALPHABET[toInt() and 0xff].toByte() and BASE64_MASK
internal fun ByteArray.encodeBase64(): String = buildPacket { writeFully(this@encodeBase64) }.encodeBase64()
internal fun String.decodeBase64Bytes(): ByteArray =
    buildPacket { writeText(dropLastWhile { it == BASE64_PAD }) }.decodeBase64Bytes()
        .readByteArray()

/**
 * Encode [bytes] as a HEX string with no spaces, newlines and `0x` prefixes.
 *
 * Taken from https://github.com/ktorio/ktor/blob/master/ktor-utils/common/src/io/ktor/util/Crypto.kt
 */
internal fun hex(bytes: ByteArray): String {
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
