package fr.xgouchet.texteditor.undo;

import android.text.Editable;
import android.util.Log;

import fr.xgouchet.texteditor.BuildConfig;

/**
 * Created by Evgenia on 05.10.2016.
 */

public class TextChangeReplace implements TextChange {

    protected StringBuffer mSequence;
    protected CharSequence mSeqBefore;
    protected int mStart;

    /**
     * @param ins
     *            the sequence being inserted
     * @param del
     *            the sequence being deleted
     * @param start
     *            the start index
     */
    public TextChangeReplace(CharSequence ins,CharSequence del, int start) {
        mSequence = new StringBuffer();
        mSequence.append(ins);
        mStart = start;
        mSeqBefore = del;
    }

    /**
     * @see fr.xgouchet.texteditor.undo.TextChange#undo(android.text.Editable)
     */
    public int undo(Editable s) {
        s.replace(mStart,mStart+mSequence.length(),mSeqBefore);
        return mStart;
    }

    /**
     * @see fr.xgouchet.texteditor.undo.TextChange#redo(android.text.Editable)
     */
    public int redo(Editable text) {
        text.replace(mStart,mStart+mSeqBefore.length(),mSequence);
        return mStart;
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
     *      int, int, int)
     */
    public boolean canMergeChangeBefore(CharSequence s, int start, int count, int after) {
        return false;
    }

    /**
     * @see fr.xgouchet.texteditor.undo.TextChange#canMergeChangeBefore(java.lang.CharSequence,
     *      int, int, int)
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
