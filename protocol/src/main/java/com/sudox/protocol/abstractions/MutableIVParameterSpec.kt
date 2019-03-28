package com.sudox.protocol.abstractions

import javax.crypto.spec.IvParameterSpec

/**
 * Абстракция параметра инициализационного вектора, позволяющая не переинициализировать Cipher
 * при изменении данного параметра.
 *
 * @param fooLength - длина IV-заглушки.
 * @author TheMax
 */
class MutableIVParameterSpec(fooLength: Int) : IvParameterSpec(ByteArray(fooLength)) {

    @JvmField
    var iv: ByteArray = super.getIV()

    /**
     * Метод переопрелен для того, чтобы Java Security API могла достать наш вектор.
     */
    override fun getIV() = iv
}