package ru.sudox.cryptography;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4ClassRunner.class)
public class RandomTest {

    @Test
    public void checkLength() {
        assertEquals(36, Random.generate(36).length);
    }

    @Test
    public void checkThatNotDuplicates() {
        byte[] first = Random.generate(36);
        byte[] second = Random.generate(36);

        assertThat(first, not(equalTo(second)));
    }
}