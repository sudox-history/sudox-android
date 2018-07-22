package com.sudox.protocol

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ProtocolKeystoreTest {

    @Test
    fun testFindKey() {
        val testRandom = "5nKoF4C/qw7WsdW1oBl/DThv9/HuFEB0Uw1Fp71khQ0="
        val testSignature = "uwT5UVSejSE/ipAQxJrM5Oeb/f36Axw2jyE/+iGG0U01KNtrwkZslD2yn7t8PchiE3aw3lOi52yRWrDmWNJQQt+6lPibGmRlSoXV2ljLkukOUmG/xeBvTIx1taSE6Q4lRjcO8pYwvH/5ivBkHPUbFHjMnNoAJscCrhKNo8SBI8NItklmIxq0ggHpIzoQvAXLx94hg8kIZZ2vBNN8G5O4J2fYsEwcN+1R2ChPUR5qwk3YDepCi1dkxVQwbJZoEOgp7DmWJZB/12nFrD90E7y0QVdzSlIAJO9P9CsrUY0Rk1FuD3qL6OAVmPEUv9a7ulrw9hGhy+j2NXTKAHKwNCypWA=="

        // Protocol keystore instance
        val protocolKeystore = ProtocolKeystore()

        // Testing
        val publicKey = protocolKeystore.findKey(testRandom, testSignature)

        // Verifying ./gradlew test connectedAndroidTest
        assertNotNull(publicKey)
    }

    @Test
    fun testFindKey_bad() {
        val testRandom = "5nKoF4C/qw7WsdW1oBl/DThv9/HuFEB0Uw1Fp71khQ0="
        val testSignature = "test"

        // Protocol keystore instance
        val protocolKeystore = ProtocolKeystore()

        // Testing
        val publicKey = protocolKeystore.findKey(testRandom, testSignature)

        // Verifying
        assertNull(publicKey)
    }
}