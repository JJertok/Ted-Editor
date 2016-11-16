package fr.xgouchet.texteditor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)

public class searchPatternTest {
    private TedActivity ted;
    
    @Before
    public void setUp() {
        ted = new TedActivity();
    }

    @Test
    public void useRegexPatternTest() {
        assertEquals("Hello!", ted.createSearchPattern("Hello!", true, false, false).pattern());
    }

    @Test
    public void wholeWordPatternTest() {
        assertEquals("\\bhello\\!\\b", ted.createSearchPattern("hello!", false, true, false).pattern());
    }

    @Test
    public void caseSensetivePatternTest() {
        assertEquals("Hello\\!", ted.createSearchPattern("Hello!", false, false, true).pattern());
    }

    @Test
    public void rgxWwPatternTest() {
        assertEquals("Hello!", ted.createSearchPattern("Hello!", true, true, false).pattern());
    }

    @Test
    public void rgxCsPatternTest() {
        assertEquals("Hello!", ted.createSearchPattern("Hello!", true, false, true).pattern());
    }

    @Test
    public void wwCsPatternTest() {
        assertEquals("\\bHello\\!\\b", ted.createSearchPattern("Hello!", false, true, true).pattern());
    }

    @Test
    public void allParamsPatternTest() {
        assertEquals("Hello!", ted.createSearchPattern("Hello!", true, true, true).pattern());
    }
}
