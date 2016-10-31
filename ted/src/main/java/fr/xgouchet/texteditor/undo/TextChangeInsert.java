package fr.xgouchet.texteditor.undo;

import fr.xgouchet.texteditor.BuildConfig;
import fr.xgouchet.texteditor.common.PageSystem;

import android.text.Editable;
import android.util.Log;
import android.util.Pair;

public class TextChangeInsert implements TextChange {

    protected StringBuffer mSequence;
    protected int mStart;
    protected int mPage;

    /**
     * @param seq   the initial sequence
     * @param start the start index for this sequence
     * @param page  number of page
     */
    public TextChangeInsert(CharSequence seq, int start, int page) {
        mSequence = new StringBuffer();
        mSequence.append(seq);
        mStart = start;
        mPage = page;
    }

    /**
     * @see fr.xgouchet.texteditor.undo.TextChange#getCaret()
     */
    public int getCaret() {
        if (mSequence.toString().contains(" "))
            return -1;
        if (mSequence.toString().contains("\n"))
            return -1;
        return mStart + mSequence.length();
    }

    /**
     * @see fr.xgouchet.texteditor.undo.TextChange#append(java.lang.CharSequence)
     */
    public void append(CharSequence seq) {
        mSequence.append(seq);
    }

    /**
     * @see fr.xgouchet.texteditor.undo.TextChange#canMergeChangeBefore(java.lang.CharSequence,
     * int, int, int)
     */
    public boolean canMergeChangeBefore(CharSequence s, int start, int count, int after) {

        CharSequence sub;
        boolean append, replace;

        if (mSequence.toString().contains(" "))
            return false;
        if (mSequence.toString().contains("\n"))
            return false;

        sub = s.subSequence(start, start + count);
        append = (start == mStart + mSequence.length());
        replace = (start == mStart) && (after >= mSequence.length())
                && (sub.toString().startsWith(mSequence.toString()));

        if (append) {
            // mSequence.append(sub);
            return true;
        }

        if (replace) {
            // mSequence = new StringBuffer();
            // mSequence.append(sub);
            return true;
        }
        return false;
    }

    /**
     * @see fr.xgouchet.texteditor.undo.TextChange#canMergeChangeAfter(java.lang.CharSequence,
     * int, int, int)
     */
    public boolean canMergeChangeAfter(CharSequence s, int start, int before, int count) {
        CharSequence sub;
        boolean append, replace;

        if (mSequence.toString().contains(" "))
            return false;
        if (mSequence.toString().contains("\n"))
            return false;

        sub = s.subSequence(start, start + count);
        if (sub.equals("()") || sub.equals("<>") || sub.equals("[]") || sub.equals("{}"))
            return false;
        append = (start == mStart + mSequence.length());
        replace = (start == mStart) && (count >= mSequence.length())
                && (sub.toString().startsWith(mSequence.toString()));

        if (append) {
            mSequence.append(sub);
            return true;
        }

        if (replace) {
            mSequence = new StringBuffer();
            mSequence.append(sub);
            return true;
        }

        return false;
    }

    /**
     * @see fr.xgouchet.texteditor.undo.TextChange#undo(java.lang.String)
     */
    public Pair<Integer, Integer> undo(PageSystem pages) {
        // if (BuildConfig.DEBUG)
        //   Log.i(TAG, "Undo Insert : deleting " + mStart + " to " + (mStart + mSequence.length()));
            StringBuilder s = new StringBuilder(pages.getPageText(mPage));
            s.replace(mStart, mStart + mSequence.length(), "");
            pages.updatePage(mPage, s.toString());
            return new Pair<>(mPage, mStart);

    }

    /**
     * @see fr.xgouchet.texteditor.redo.TextChange#redo(java.lang.String)
     */
    public Pair<Integer, Integer> redo(PageSystem pages) {
            StringBuilder s = new StringBuilder(pages.getPageText(mPage));
            s.insert(mStart, mSequence);
            pages.updatePage(mPage, s.toString());
            return new Pair<>(mPage, mStart + mSequence.length());
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "+\"" + mSequence.toString().replaceAll("\n", "~") + "\" @" + mStart;
    }

}
