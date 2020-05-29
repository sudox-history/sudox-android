package ru.sudox.cryptography;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.sudox.cryptography.entries.KeyPair;
import ru.sudox.cryptography.entries.SecretKeyPair;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static ru.sudox.cryptography.X25519.PUBLIC_KEY_LENGTH;
import static ru.sudox.cryptography.X25519.SECRET_KEY_LENGTH;

@RunWith(AndroidJUnit4ClassRunner.class)
public class X25519Test {

    @Test
    public void testPairGeneration() {
        KeyPair pair = X25519.generateKeyPair();

        assertNotNull(pair.getPublicKey());
        assertEquals(PUBLIC_KEY_LENGTH, pair.getPublicKey().length);

        assertNotNull(pair.getSecretKey());
        assertEquals(SECRET_KEY_LENGTH, pair.getSecretKey().length);
    }

    @Test
    public void testExchangingWhenKeysValid() throws Exception {
        KeyPair first = X25519.generateKeyPair();
        KeyPair second = X25519.generateKeyPair();

        SecretKeyPair firstSecret = X25519.exchange(first.getPublicKey(), first.getSecretKey(), second.getPublicKey(), true);
        SecretKeyPair secondSecret = X25519.exchange(second.getPublicKey(), second.getSecretKey(), first.getPublicKey(), false);

        assertArrayEquals(firstSecret.getReceiveKey(), secondSecret.getSendKey());
        assertArrayEquals(secondSecret.getReceiveKey(), firstSecret.getSendKey());
    }
}