package com.sudox.protocol

import androidx.test.runner.AndroidJUnit4
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProtocolKeystoreTest {

    @Test
    fun testFindKey() {
        val testRandom = "be975299a253ee36bf6d8cfd96b5ae27edab9e01fb611c563b49a00cc2d9fc97"
        val testSignature = "807a6a64fdcc468893745caf68a404ddfb6f4b524e3d027490beb4d8002ba55cb390025c2f8a4721a7f0db4baa99c5786c62a5d492d490b477ccc279b79a6a7cd51be2d43fded90914581a151a689d85b0a9e20cd4975265d302729977cd67ef464e2ba781ace8b49ed0e71db472c4c5ce1aecd43e59085b27c6c3092cb8e4e0a0e8b6354767e7196a4cc6bc81f66d3f94cb11a5d5f7c0b0f2290bcd847f729a68e9541aaa4dc22f84075bc372edce4bdf76673f5bd6a702a66c0d869dae6995079088ac8b0bb0e2b9d1fb7e08c8c98c2c556ae33be7fa87179fc90897979a8f6250957bcc7209eb2997aca65f7139b516c8c0479507ac536cbe9862e3a53353"
                .toByteArray()

        // Protocol keystore instance
        val protocolKeystore = ProtocolKeystore()

        // Testing
        val publicKey = protocolKeystore.findKey(testRandom, testSignature)

        assertNotNull(publicKey)
    }
}