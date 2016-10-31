package fr.xgouchet.texteditor.undo;

import fr.xgouchet.texteditor.BuildConfig;
import fr.xgouchet.texteditor.common.PageSystem;

import android.text.Editable;
import android.util.Log;
import android.util.Pair;

public class TextChangeDelete implements TextChange {

    protected StringBuffer mSequence;
    protected int mStart;
    protected int mPage;

    /**
     * @param seq   the sequence being deleted
     * @param start the start index
     * @param page  number of page
     */
    public TextChangeDelete(CharSequence seq, int start, int page) {
        mSequence = new StringBuffer();
        mSequence.append(seq);
        mStart = start;
        mPage = page;
    }

    /**
     * @see fr.xgouchet.texteditor.undo.TextChange#undo(android.text.Editable)
     */
    public Pair<Integer, Integer> undo(PageSystem pages) {
            StringBuilder s = new StringBuilder(pages.getPageText(mPage));
            s.insert(mStart, mSequence);
            pages.updatePage(mPage, s.toString());
            return new Pair<>(mPage, mStart + mSequence.length());

    }

    /**
     * @see fr.xgouchet.texteditor.redo.TextChange#redo(android.text.Editable)
     */
    public Pair<Integer, Integer> redo(PageSystem pages) {
            StringBuilder s = new StringBuilder(pages.getPageText(mPage));
            s.replace(mStart, mStart + mSequence.length(), "");
            pages.updatePage(mPage, s.toString());
            return new Pair<>(mPage, mStart);

    }

    /**
     * @see fr.xgouchet.texteditor.undo.TextChange#getCaret()
     */
    public int getCaret() {
        if (mSequence.toString().contains(" "))
            return -1;
        if (mSequence.toString().contains("\n"))
            return -1;
        return mStart;
    }

    /**
     * @see fr.xgouchet.texteditor.undo.TextChange#append(java.lang.CharSequence)
     */
    public void append(CharSequence seq) {
        mSequence.insert(0, seq);
        if (BuildConfig.DEBUG)
            Log.d(TAG, mSequence.toString());
        mStart -= seq.length();
    }

    /**
     * @see fr.xgouchet.texteditor.undo.TextChange#canMergeChangeAfter(java.lang.CharSequence,
     * int, int, int)
     */
    public boolean canMergeChangeBefore(CharSequence s, int start, int count, int after) {
        CharSequence sub;
        if (mSequence.toString().contains(" "))
            return false;
        if (mSequence.toString().contains("\n"))
            return false;
        if ((count != 1) || (start + count != mStart))
            return false;

        sub = s.subSequence(start, start + count);
        append(sub);
        return true;
    }

    /**
     * @see fr.xgouchet.texteditor.undo.TextChange#canMergeChangeBefore(java.lang.CharSequence,
     * int, int, int)
     */
    public boolean canMergeChangeAfter(CharSequence s, int start, int before, int count) {
        return false;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "-\"" + mSequence.toString().replaceAll("\n", "~") + "\" @" + mStart;
    }

}
