package fr.xgouchet.texteditor;

import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)

public class bracketsTest {

    private TedActivity ted;

    @Before
    public void setUp() {
        ted = new TedActivity();
    }

    @Test
    public void bracket1Test() {
        assertEquals(")", ted.bracketsController("Brackets(",8));
    }

    @Test
    public void bracket2Test() {
        assertEquals("}", ted.bracketsController("Brackets{",8));
    }

    @Test
    public void bracket3Test() {
        assertEquals(">", ted.bracketsController("Brackets<",8));
    }

    @Test
    public void bracket4Test() {
        assertEquals("]", ted.bracketsController("Brackets[",8));
    }
}