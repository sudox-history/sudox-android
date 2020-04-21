package ru.sudox.cryptography;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Base64;

import static org.junit.Assert.assertArrayEquals;

@RunWith(AndroidJUnit4ClassRunner.class)
public class BLAKE2bTest {

    @Test
    public void testHashing() {
        byte[] valid = Base64.getDecoder().decode("v1bAco/U6c9kv69tq6uBVUEDKYze5cxNWAQzqiXpiwA=");
        byte[] data = BLAKE2b.hash("Hello World!".getBytes());

        assertArrayEquals(valid, data);
    }
}
