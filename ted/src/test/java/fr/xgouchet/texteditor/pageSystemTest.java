package fr.xgouchet.texteditor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;

import fr.xgouchet.texteditor.common.PageSystem;
import fr.xgouchet.texteditor.common.TextFileUtils;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)

public class pageSystemTest {
    private PageSystem mPages;

    @Before
    public void setUp() {
        mPages = new PageSystem("");
    }

    @Test
    public void initializationTest() {
        mPages = new PageSystem("");
        assertEquals(0, mPages.getMaxPage());
        String s = TextFileUtils.readTextFile(new File("e:\\TedActivity.java"));
        mPages.reInitPageSystem(s);
        assertEquals(9, mPages.getMaxPage());
    }

    @Test
    public void pageChangingSystem() {
        String s = TextFileUtils.readTextFile(new File("e:\\TedActivity.java"));
        mPages.reInitPageSystem(s);
        mPages.goToPage(0);

        assertEquals(0, mPages.getCurrentPage());

        mPages.goToPage(2);
        assertEquals(2, mPages.getCurrentPage());

        mPages.goToPage(7);
        assertEquals(7, mPages.getCurrentPage());
    }

    @Test
    public void checkNextPrevPages() {
        String s = TextFileUtils.readTextFile(new File("e:\\TedActivity.java"));
        mPages.reInitPageSystem(s);

        mPages.goToPage(0);
        assertFalse(mPages.canReadPrevPage());
        assertTrue(mPages.canReadNextPage());

        mPages.goToPage(mPages.getMaxPage());

        assertTrue(mPages.canReadPrevPage());
        assertFalse(mPages.canReadNextPage());
    }

    @Test
    public void startingLineCalculations() {
        String s = TextFileUtils.readTextFile(new File("e:\\TedActivity.java"));
        mPages.reInitPageSystem(s);
        mPages.goToPage(0);
        assertEquals(0, mPages.getStartingLine());
        mPages.goToPage(1);
        assertEquals(200, mPages.getStartingLine());
        mPages.goToPage(7);
        assertEquals(1400, mPages.getStartingLine());

    }
}

