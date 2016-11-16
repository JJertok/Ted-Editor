package fr.xgouchet.texteditor;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class searchMatchesTest {
    private TedActivity ted;

    @Before
    public void setUp() {
        ted = new TedActivity();
    }

    @Test
    public void searchMatchesCountTest() {

        assertEquals(2, ted.checkAllMatches("Hello, heLlo ,Hello hello w hello", ted.createSearchPattern("hello", false, true, true)).size());
        assertEquals(2, ted.checkAllMatches("hallo, heLlo ,Hello hullo,1 hello", ted.createSearchPattern("h.l*.,", true, false, false)).size());

    }
}
