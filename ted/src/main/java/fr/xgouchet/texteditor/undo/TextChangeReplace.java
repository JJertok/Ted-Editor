package fr.xgouchet.texteditor.undo;

import android.text.Editable;
import android.util.Log;
import android.util.Pair;

import fr.xgouchet.texteditor.BuildConfig;
import fr.xgouchet.texteditor.common.PageSystem;

/**
 * Created by Evgenia on 05.10.2016.
 */

public class TextChangeReplace implements TextChange {

    protected StringBuffer mSequence;
    protected CharSequence mSeqBefore;
    protected int mStart;
    protected int mPage;

    /**
     * @param ins   the sequence being inserted
     * @param del   the sequence being deleted
     * @param start the start index
     * @param page  number of page
     */
    public TextChangeReplace(CharSequence ins, CharSequence del, int start, int page) {
        mSequence = new StringBuffer();
        mSequence.append(ins);
        mStart = start;
        mSeqBefore = del;
        mPage = page;
    }

    /**
     * @see fr.xgouchet.texteditor.undo.TextChange#(android.text.Editable)
     */
    public Pair<Integer, Integer> undo(PageSystem pages) {
        if (mPage == -2 && mStart == -1) {
            pages.reInitPageSystem(mSeqBefore.toString());
            return new Pair<>(mPage, mStart);
        } else {
            StringBuilder s = new StringBuilder(pages.getPageText(mPage));
            s.replace(mStart, mStart + mSequence.length(), mSeqBefore.toString());
            pages.updatePage(mPage, s.toString());
            return new Pair<>(mPage, mStart);
        }
    }

    /**
     * @see fr.xgouchet.texteditor.undo.TextChange#(android.text.Editable)
     */
    public Pair<Integer, Integer> redo(PageSystem pages) {
        if (mPage == -2 && mStart == -1) {
            pages.reInitPageSystem(mSequence.toString());
            return new Pair<>(mPage, mStart);
        } else {
            StringBuilder s = new StringBuilder(pages.getPageText(mPage));
            s.replace(mStart, mStart + mSeqBefore.length(), mSequence.toString());
            pages.updatePage(mPage, s.toString());
            return new Pair<>(mPage, mStart);
        }
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
        return false;
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
        return "*\"" + mSequence.toString().replaceAll("\n", "~") + "\" @" + mStart;
    }

}
