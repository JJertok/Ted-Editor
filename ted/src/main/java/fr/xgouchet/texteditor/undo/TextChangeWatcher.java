package fr.xgouchet.texteditor.undo;

import java.util.Stack;

import android.text.Editable;
import android.util.Log;
import android.util.Pair;

import fr.xgouchet.texteditor.BuildConfig;
import fr.xgouchet.texteditor.common.Constants;
import fr.xgouchet.texteditor.common.PageSystem;
import fr.xgouchet.texteditor.common.Settings;

public class TextChangeWatcher implements Constants {

    /**
     *
     */
    public TextChangeWatcher() {
        mChanges = new Stack<TextChange>();
        mCancelledChanges = new Stack<TextChange>();
    }

    /**
     * Undo the last operation
     *
     * @param pages the text to undo on
     * @return the caret position
     */
    public Pair<Integer, Integer> undo(PageSystem pages) {
        pushCurrentChange();

        if (mChanges.size() == 0) {
            if (BuildConfig.DEBUG)
                Log.i(TAG, "Nothing to undo");
            return new Pair<>(-1, -1);
        }

        TextChange change = mChanges.pop();
        if (change != null) {
            mCancelledChanges.push(change);
            return change.undo(pages);
        } else if (BuildConfig.DEBUG)
            Log.w(TAG, "Null change ?!");

        return new Pair<>(-1, -1);
    }

    /**
     * Redo the last operation
     *
     * @param pages the text to redo on
     * @return the caret position
     */
    public Pair<Integer, Integer> redo(PageSystem pages) {
        if (mCancelledChanges.size() == 0) {
            if (BuildConfig.DEBUG)
                Log.i(TAG, "Nothing to redo");
            return new Pair<>(-1, -1);
        }

        TextChange cancelledChange = mCancelledChanges.pop();
        if (cancelledChange != null) {
            mChanges.push(cancelledChange);
            return cancelledChange.redo(pages);
        } else if (BuildConfig.DEBUG)
            Log.w(TAG, "Null change ?!");

        return new Pair<>(-1, -1);
    }

    /**
     * A change to the text {@linkplain } will be made, where the
     * {@linkplain } characters starting at {@linkplain } will be
     * replaced by {@linkplain } characters
     *
     * @param s     the sequence being changed
     * @param start the start index
     * @param count the number of characters that will change
     * @param after the number of characters that will replace the old ones
     * @param page  number of page
     */
    public void beforeChange(CharSequence s, int start, int count, int after, int page) {
        if ((mCurrentChange != null)
                && (
                mCurrentChange.canMergeChangeBefore(s, start, count, after))) {
        } else {
            if (count == 0) {
                // no existing character changed
                // ignore, will be processed after
            } else if (after == 0) {
                // existing character replaced by none => delete
                processDelete(s, start, count, page);
            } else {
                // n chars replaced by m other chars => replace
                // replace is a delete AND an insert...
                processDelete(s, start, count, page);
            }
        }
    }

    /**
     * A change to the text {@linkplain } has been made, where the
     * {@linkplain } characters starting at {@linkplain } have
     * replaced the substring of length {@linkplain }
     *
     * @param s      the sequence being changed
     * @param start  the start index
     * @param before the number of character that were replaced
     * @param count  the number of characters that will change
     * @param page   number of page
     */
    public void afterChange(CharSequence s, int start, int before, int count, int page) {
        if ((mCurrentChange != null)
                && (mCurrentChange.canMergeChangeAfter(s, start, before, count))) {

        } else {
            if (before == 0) {
                // 0 charactes replaced by count => insert
                processInsert(s, start, count, page);
            } else if (count == 0) {
                // existing character replaced by none => delete, already done
                // before
            } else {
                // n chars replaced by m other chars => replace
                // replace is a delete AND an insert...
                processInsert(s, start, count, page);
            }
        }

        // printStack();
    }

    /**
     * @param s     the sequence being modified
     * @param start the first character index
     * @param count the number of inserted text
     * @param page  number of page
     */
    public void processInsert(CharSequence s, int start, int count, int page) {
        CharSequence sub = s.subSequence(start, start + count);

        if (mCurrentChange != null)
            pushCurrentChange();

        mCurrentChange = new TextChangeInsert(sub, start, page);
    }

    /**
     * @param s     the sequence being modified
     * @param start the first character index
     * @param count the number of inserted text
     * @param page  number of page
     */
    public void processDelete(CharSequence s, int start, int count, int page) {
        CharSequence sub = s.subSequence(start, start + count);

        if (mCurrentChange != null)
            pushCurrentChange();

        mCurrentChange = new TextChangeDelete(sub, start, page);
    }


    /**
     * @param ins   the sequence being inserted
     * @param del   the sequence before modified
     * @param start the index where replacing starts
     * @param page  number of page
     */
    public void processReplace(CharSequence ins, CharSequence del, int start, int page) {
        if (mCurrentChange != null)
            pushCurrentChange();

        mCurrentChange = new TextChangeReplace(ins, del, start, page);
    }

    public void processReplaceAll(CharSequence ins, CharSequence del) {
        if (mCurrentChange != null)
            pushCurrentChange();

        mCurrentChange = new TextChangeReplace(ins, del, -1, -2);
    }


    /**
     * Pushes the current change on top of the stack
     */
    protected void pushCurrentChange() {
        if (mCurrentChange == null)
            return;

        if (mChanges.size() == 0 || !mChanges.peek().equals(mCurrentChange)) {
            mChanges.push(mCurrentChange);
        }
        while (mChanges.size() > Settings.UNDO_MAX_STACK) {
            mChanges.remove(0);
        }
        mCurrentChange = null;
        mCancelledChanges.clear();
    }

    /**
     * Prints the current stack
     */
    public void printStack() {
        if (!BuildConfig.DEBUG)
            return;
        Log.i(TAG, "STACK");
        for (TextChange change : mChanges) {
            Log.d(TAG, change.toString());
        }
        Log.d(TAG, "Current change : " + mCurrentChange.toString());
    }

    public TextChange mCurrentChange;
    public final Stack<TextChange> mChanges;
    public final Stack<TextChange> mCancelledChanges;
}
