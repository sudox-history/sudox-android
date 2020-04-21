package ru.sudox.cryptography;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.SecureRandom;

import static org.junit.Assert.assertArrayEquals;
import static ru.sudox.cryptography.XChaCha20Poly1305.KEY_LENGTH;
import static ru.sudox.cryptography.XChaCha20Poly1305.NONCE_LENGTH;

@RunWith(AndroidJUnit4ClassRunner.class)
public class XChaCha20Poly1305Test {

    @Test
    public void testIntegration() throws Exception {
        byte[] data = "Hello World!".getBytes();
        byte[] key = new byte[KEY_LENGTH];
        byte[] nonce = new byte[NONCE_LENGTH];

        SecureRandom random = SecureRandom.getInstanceStrong();
        random.nextBytes(key);
        random.nextBytes(nonce);

        byte[] cipherText = XChaCha20Poly1305.encryptData(key, nonce, data);
        byte[] decrypted = XChaCha20Poly1305.decryptData(key, nonce, cipherText);

        assertArrayEquals(decrypted, data);
    }

    @Test(expected = SecurityException.class)
    public void testDecryptionWhenKeyInvalid() throws Exception {
        byte[] data = "Hello World!".getBytes();
        byte[] key = new byte[KEY_LENGTH];
        byte[] nonce = new byte[NONCE_LENGTH];

        SecureRandom random = SecureRandom.getInstanceStrong();
        random.nextBytes(key);
        random.nextBytes(nonce);

        byte[] cipherText = XChaCha20Poly1305.encryptData(key, nonce, data);
        random.nextBytes(key);
        XChaCha20Poly1305.decryptData(key, nonce, cipherText);
    }

    @Test(expected = SecurityException.class)
    public void testDecryptionWhenNonceInvalid() throws Exception {
        byte[] data = "Hello World!".getBytes();
        byte[] key = new byte[KEY_LENGTH];
        byte[] nonce = new byte[NONCE_LENGTH];

        SecureRandom random = SecureRandom.getInstanceStrong();
        random.nextBytes(key);
        random.nextBytes(nonce);

        byte[] cipherText = XChaCha20Poly1305.encryptData(key, nonce, data);
        random.nextBytes(nonce);
        XChaCha20Poly1305.decryptData(key, nonce, cipherText);
    }
}