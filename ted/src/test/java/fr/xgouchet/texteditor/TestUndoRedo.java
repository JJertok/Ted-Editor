package fr.xgouchet.texteditor;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import org.junit.Before;
import org.junit.Test;

import fr.xgouchet.texteditor.common.PageSystem;
import fr.xgouchet.texteditor.undo.TextChangeWatcher;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)//"src/main/AndroidManifest.xml")


public class TestUndoRedo extends TextChangeWatcher {
   private TextChangeWatcher textWatcher;

    @Before
    public void setUp() {
        textWatcher = new TextChangeWatcher();
    }

    @Test
    public void testUndo() {
        assertNotNull(textWatcher);
        CharSequence s1= "Hello!";
        PageSystem a  = new PageSystem("");
        textWatcher.processInsert(s1,0,s1.length(),0);
        textWatcher.processInsert(s1,0,s1.length(),0);
        textWatcher.processInsert(s1,0,s1.length(),0);
        textWatcher.processDelete(s1,0,s1.length(),0);
        textWatcher.undo(a);
        textWatcher.undo(a);
        assertEquals(2,textWatcher.mChanges.size());
        textWatcher.undo(a);
        assertEquals(1,textWatcher.mChanges.size());
    }

    @Test
    public void testRedo() {
        assertNotNull(textWatcher);
        CharSequence s1= "Hello!";
        PageSystem a  = new PageSystem("");
        textWatcher.processInsert(s1,0,s1.length(),0);
        textWatcher.processInsert(s1,0,s1.length(),0);
        textWatcher.processInsert(s1,0,s1.length(),0);
        textWatcher.processDelete(s1,0,s1.length(),0);
        textWatcher.undo(a);
        textWatcher.undo(a);
        assertEquals(2,textWatcher.mChanges.size());
        textWatcher.redo(a);
        assertEquals(3,textWatcher.mChanges.size());
    }
}
