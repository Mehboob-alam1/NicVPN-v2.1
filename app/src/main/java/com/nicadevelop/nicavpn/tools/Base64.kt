package com.nicadevelop.nicavpn.tools

import java.io.UnsupportedEncodingException


public class Base64 {
    private val b64: ByteArray =
        str2byte("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=")!!

    private fun valueValidate(foo: Byte): Byte {
        if (foo.toInt() == 61) {
            return 0
        }
        for (j in b64.indices) {
            if (foo == b64[j]) {
                return j.toByte()
            }
        }
        return 0
    }

    @Throws(IllegalStateException::class)
    fun fromBase64(buf: ByteArray, start: Int, length: Int): ByteArray? {
        return try {
            val foo = ByteArray(length)
            var j = 0
            var i = start
            while (true) {
                if (i >= start + length) {
                    break
                }
                foo[j] =
                    (valueValidate(buf[i]).toInt() shl 2 or (valueValidate(buf[i + 1]).toInt() and 48 ushr 4)) as Byte
                if (buf[i + 2].toInt() == 61) {
                    j++
                    break
                }
                foo[j + 1] =
                    (valueValidate(buf[i + 1]).toInt() and 15 shl 4 or (valueValidate(buf[i + 2]).toInt() and 60 ushr 2)) as Byte
                if (buf[i + 3].toInt() == 61) {
                    j += 2
                    break
                }
                foo[j + 2] =
                    (valueValidate(buf[i + 2]).toInt() and 3 shl 6 or (valueValidate(buf[i + 3]).toInt() and 63)) as Byte
                j += 3
                i += 4
            }
            val bar = ByteArray(j)
            System.arraycopy(foo, 0, bar, 0, j)
            bar
        } catch (e: ArrayIndexOutOfBoundsException) {
            throw IllegalStateException("fromBase64: invalid base64 data", e)
        }
    }

    fun toBase64(buf: ByteArray, start: Int, length: Int): ByteArray? {
        val i: Int
        val tmp = ByteArray(length * 2)
        val foo = length / 3 * 3 + start
        var j = start
        var i2 = 0
        while (j < foo) {
            val i3 = i2 + 1
            tmp[i2] = b64.get(buf[j].toInt() ushr 2 and 63)
            val i4 = i3 + 1
            tmp[i3] = b64.get(buf[j].toInt() and 3 shl 4 or (buf[j + 1].toInt() ushr 4 and 15))
            val i5 = i4 + 1
            tmp[i4] = b64.get(buf[j + 1].toInt() and 15 shl 2 or (buf[j + 2].toInt() ushr 6 and 3))
            i2 = i5 + 1
            tmp[i5] = b64.get(buf[j + 2].toInt() and 63)
            j += 3
        }
        val foo2 = start + length - foo
        if (foo2 == 1) {
            val i6 = i2 + 1
            tmp[i2] = b64.get(buf[j].toInt() ushr 2 and 63)
            val i7 = i6 + 1
            tmp[i6] = b64.get(buf[j].toInt() and 3 shl 4 and 63)
            val i8 = i7 + 1
            tmp[i7] = 61
            val i9 = i8 + 1
            tmp[i8] = 61
            i = i9
        } else {
            if (foo2 == 2) {
                val i10 = i2 + 1
                tmp[i2] = b64.get(buf[j].toInt() ushr 2 and 63)
                val i11 = i10 + 1
                tmp[i10] = b64.get(buf[j].toInt() and 3 shl 4 or (buf[j + 1].toInt() ushr 4 and 15))
                val i12 = i11 + 1
                tmp[i11] = b64.get(buf[j + 1].toInt() and 15 shl 2 and 63)
                i2 = i12 + 1
                tmp[i12] = 61
            }
            i = i2
        }
        val bar = ByteArray(i)
        System.arraycopy(tmp, 0, bar, 0, i)
        return bar
    }

    fun str2byte(str: String?): ByteArray? {
        return str2byte(str, "UTF-8")
    }

    fun str2byte(str: String?, encoding: String?): ByteArray? {
        return if (str == null) {
            null
        } else try {
            str.toByteArray(charset(encoding!!))
        } catch (e: UnsupportedEncodingException) {
            str.toByteArray()
        }
    }

    fun byte2str(str: ByteArray): String? {
        return byte2str(str, 0, str.size, "UTF-8")
    }

    fun byte2str(str: ByteArray, encoding: String?): String? {
        return byte2str(str, 0, str.size, encoding)
    }

    fun byte2str(str: ByteArray?, s: Int, l: Int, encoding: String?): String? {
        return try {
            String(str!!, s, l, charset(encoding!!))
        } catch (e: UnsupportedEncodingException) {
            String(str!!, s, l)
        }
    }
}