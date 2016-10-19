package fr.xgouchet.texteditor.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Scroller;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import fr.xgouchet.texteditor.R;
import fr.xgouchet.texteditor.common.Constants;
import fr.xgouchet.texteditor.common.PageSystem;
import fr.xgouchet.texteditor.common.Settings;
import fr.xgouchet.texteditor.ui.listener.UpdateSettingListener;

/**
 * TODO create a syntax highlighter
 */
public class AdvancedEditText extends EditText implements Constants,
        OnKeyListener, OnGestureListener {

    /**
     * @param context the current context
     * @param attrs   some attributes
     * @category ObjectLifecycle
     */

    public AdvancedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);


        this.setDrawingCacheEnabled(true);
        mPaintNumbers = new Paint();
        mPaintNumbers.setTypeface(Typeface.MONOSPACE);
        mPaintNumbers.setAntiAlias(true);

        mPaintHighlight = new Paint();

        mPaintHighlightLine = new Paint();

        startLineNumber = 0;

        mScale = context.getResources().getDisplayMetrics().density;
        mPadding = (int) (mPaddingDP * mScale);

        mHighlightedLine = mHighlightStart = -1;

        mDrawingRect = new Rect();
        mLineBounds = new Rect();

        mGestureDetector = new GestureDetector(getContext(), this);

        updateFromSettings();

    }

    LinkedList<UpdateSettingListener> listeners = new LinkedList<UpdateSettingListener>();


    public void addUpdateSetting(UpdateSettingListener listener) {
        listeners.add(listener);
    }

    /**
     * @category View
     * @see android.widget.TextView#computeScroll()
     */
    public void computeScroll() {

        if (mTedScroller != null) {
            if (mTedScroller.computeScrollOffset()) {
                scrollTo(mTedScroller.getCurrX(), mTedScroller.getCurrY());
            }
        } else {
            super.computeScroll();
        }
    }

    /**
     * @category View
     * @see EditText#onDraw(Canvas)
     */
    public void onDraw(Canvas canvas) {
        int count, lineX, baseline;
        count = getLineCount();

        if (Settings.SHOW_LINE_NUMBERS) {
            int padding = (int) (Math.floor(Math.log10(count+startLineNumber)) + 1);
            padding = (int) ((padding * mPaintNumbers.getTextSize()) + mPadding + (Settings.TEXT_SIZE
                    * mScale * 0.5));
            if (mLinePadding != padding) {
                mLinePadding = padding;
                setPadding(mLinePadding, mPadding, mPadding, mPadding);
            }
        }

        // get the drawing boundaries
        getDrawingRect(mDrawingRect);

        // display current line
        computeLineHighlight();

        // draw line numbers
        lineX = (int) (mDrawingRect.left + mLinePadding - (Settings.TEXT_SIZE
                * mScale * 0.5));
        int min = 0;
        int max = count;
        getLineBounds(0, mLineBounds);
        int startBottom = mLineBounds.bottom;
        int startTop = mLineBounds.top;
        getLineBounds(count - 1, mLineBounds);
        int endBottom = mLineBounds.bottom;
        int endTop = mLineBounds.top;
        if (count > 1 && endBottom > startBottom && endTop > startTop) {
            min = Math.max(min, ((mDrawingRect.top - startBottom) * (count - 1)) / (endBottom - startBottom));
            max = Math.min(max, ((mDrawingRect.bottom - startTop) * (count - 1)) / (endTop - startTop) + 1);
        }
        for (int i = min; i < max; i++) {
            baseline = getLineBounds(i, mLineBounds);

            if ((mMaxSize != null) && (mMaxSize.x < mLineBounds.right)) {
                mMaxSize.x = mLineBounds.right;
            }

            if ((i == mHighlightedLine) && (!Settings.WORDWRAP)) {
                canvas.drawRect(mLineBounds, mPaintHighlight);
            }

            if (Settings.SHOW_LINE_NUMBERS) {
                canvas.drawText("" + (i + startLineNumber + 1), mDrawingRect.left + mPadding,
                        baseline, mPaintNumbers);

            }
            if (Settings.SHOW_LINE_NUMBERS) {
                canvas.drawLine(lineX, mDrawingRect.top, lineX,
                        mDrawingRect.bottom, mPaintNumbers);
            }
        }
        getLineBounds(count - 1, mLineBounds);
        if (mMaxSize != null) {
            mMaxSize.y = mLineBounds.bottom;
            mMaxSize.x = Math.max(mMaxSize.x + mPadding - mDrawingRect.width(),
                    0);
            mMaxSize.y = Math.max(
                    mMaxSize.y + mPadding - mDrawingRect.height(), 0);
        }
        drawHightlightLines(canvas);

        super.onDraw(canvas);

    }

    /**
     * @see android.view.View.OnKeyListener#onKey(android.view.View, int,
     * android.view.KeyEvent)
     */
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    /**
     * @category GestureDetection
     * @see android.widget.TextView#onTouchEvent(android.view.MotionEvent)
     */
    public boolean onTouchEvent(MotionEvent event) {

        // check for tap and cancel fling
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
            if (mTedScroller != null)
                if (!mTedScroller.isFinished()) {
                    mTedScroller.abortAnimation();
                }
        }
        super.onTouchEvent(event);
        if (mGestureDetector != null) {
            return mGestureDetector.onTouchEvent(event);
        }

        return true;
    }

    /**
     * @category GestureDetection
     * @see android.view.GestureDetector.OnGestureListener#onDown(android.view.MotionEvent)
     */
    public boolean onDown(MotionEvent e) {
        return true;
    }

    /**
     * @category GestureDetection
     * @see android.view.GestureDetector.OnGestureListener#onSingleTapUp(android.view.MotionEvent)
     */
    public boolean onSingleTapUp(MotionEvent e) {
        if (isEnabled()) {
            ((InputMethodManager) getContext().getSystemService(
                    Context.INPUT_METHOD_SERVICE)).showSoftInput(this,
                    InputMethodManager.SHOW_IMPLICIT);
        }
        return true;
    }

    /**
     * @category GestureDetection
     * @see android.view.GestureDetector.OnGestureListener#onShowPress(android.view.MotionEvent)
     */
    public void onShowPress(MotionEvent e) {
    }

    /**
     * @see android.view.GestureDetector.OnGestureListener#onLongPress(android.view.MotionEvent)
     */
    public void onLongPress(MotionEvent e) {

    }

    /**
     * @see android.view.GestureDetector.OnGestureListener#onScroll(android.view.MotionEvent,
     * android.view.MotionEvent, float, float)
     */
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        // mTedScroller.setFriction(0);

        return true;

    }

    /**
     * @see android.view.GestureDetector.OnGestureListener#onFling(android.view.MotionEvent,
     * android.view.MotionEvent, float, float)
     */
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        if (!Settings.FLING_TO_SCROLL) {
            return true;
        }

        if (mTedScroller != null) {
            mTedScroller.fling(getScrollX(), getScrollY(), -(int) velocityX,
                    -(int) velocityY, 0, mMaxSize.x, 0, mMaxSize.y);
        }
        return true;
    }

    /**
     * Update view settings from the app preferences
     *
     * @category Custom
     */
    public void updateFromSettings() {
        for (UpdateSettingListener listener :
                listeners) {
            listener.updateSetting();
        }

        if (isInEditMode()) {
            return;
        }

        setTypeface(Settings.getTypeface(getContext()));

        // wordwrap
        setHorizontallyScrolling(!Settings.WORDWRAP);


        // color Theme
        switch (Settings.COLOR) {
            case COLOR_NEGATIVE:
                setBackgroundResource(R.drawable.textfield_black);
                setTextColor(Color.WHITE);
                mPaintHighlight.setColor(Color.WHITE);
                mPaintNumbers.setColor(Color.GRAY);
                mPaintHighlightLine.setColor(Color.WHITE);
                break;
            case COLOR_MATRIX:
                setBackgroundResource(R.drawable.textfield_matrix);
                setTextColor(Color.GREEN);
                mPaintHighlight.setColor(Color.GREEN);
                mPaintNumbers.setColor(Color.rgb(0, 128, 0));
                mPaintHighlightLine.setColor(Color.GREEN);
                break;
            case COLOR_SKY:
                setBackgroundResource(R.drawable.textfield_sky);
                setTextColor(Color.rgb(0, 0, 64));
                mPaintHighlight.setColor(Color.rgb(0, 0, 64));
                mPaintNumbers.setColor(Color.rgb(0, 128, 255));
                mPaintHighlightLine.setColor(Color.rgb(255, 127, 39));
                break;
            case COLOR_DRACULA:
                setBackgroundResource(R.drawable.textfield_dracula);
                setTextColor(Color.RED);
                mPaintHighlight.setColor(Color.RED);
                mPaintNumbers.setColor(Color.rgb(192, 0, 0));
                mPaintHighlightLine.setColor(Color.RED);
                break;
            case COLOR_CLASSIC:
            default:
                setBackgroundResource(R.drawable.textfield_white);
                setTextColor(Color.BLACK);
                mPaintHighlight.setColor(Color.BLACK);
                mPaintNumbers.setColor(Color.GRAY);
                mPaintHighlightLine.setColor(Color.BLACK);
                break;
        }
        mPaintHighlight.setAlpha(48);

        // text size
        setTextSize(Settings.TEXT_SIZE);
        mPaintNumbers.setTextSize(Settings.TEXT_SIZE * mScale * 0.85f);

        // refresh view
        postInvalidate();
        refreshDrawableState();

        // use Fling when scrolling settings ?
        if (Settings.FLING_TO_SCROLL) {
            mTedScroller = new Scroller(getContext());
            mMaxSize = new Point();
        } else {
            mTedScroller = null;
            mMaxSize = null;
        }

        // padding
        mLinePadding = mPadding;
        int count = getLineCount();
        if (Settings.SHOW_LINE_NUMBERS) {
            mLinePadding = (int) (Math.floor(Math.log10(count)) + 1);
            mLinePadding = (int) ((mLinePadding * mPaintNumbers.getTextSize())
                    + mPadding + (Settings.TEXT_SIZE * mScale * 0.5));
            setPadding(mLinePadding, mPadding, mPadding, mPadding);
        } else {
            setPadding(mPadding, mPadding, mPadding, mPadding);
        }

    }

    /**
     * Set number of line for hightlights
     *
     * @param numberOfLines
     */
    public void setHighlightLines(Set<Integer> numberOfLines) {
        mHighlightLines = numberOfLines;
    }

    /**
     * Set hightlight matches
     */
    public void setHighlightMatches(ArrayList<Pair<Integer, Integer>> matches) {
        if (mHighlightLines == null) mHighlightLines = new HashSet<Integer>();
        else mHighlightLines.clear();

        String text = getText().toString();
        for (Pair<Integer, Integer> selection :
                matches) {
            mHighlightLines.add(getHighlightLine(text, selection.first));
        }
    }

    /**
     * Clear all highlights lines
     */
    public void clearHighlightLines() {
        mHighlightLines.clear();
    }

    /**
     * Draw setted highlight lines
     *
     * @param canvas
     */
    private void drawHightlightLines(Canvas canvas) {
        // get number of lines
        int count = getLineCount();
        if (count == 0 || mHighlightLines == null) return;

        // get the drawing boundaries, rect changing offset if textview had scrolled
        getDrawingRect(mDrawingRect);

        int height = canvas.getHeight();
        int width = canvas.getWidth();

        float position = 0;

        // draw a highlight labels near a scrollbar area
        for (Integer i :
                mHighlightLines) {
            if (i < 0 || i >= count) continue;
            position = (i / (float) count) * height + mDrawingRect.top;
            canvas.drawLine(canvas.getWidth() + mDrawingRect.left - 20, position, canvas.getWidth() + mDrawingRect.left, position, mPaintHighlightLine);
            canvas.drawLine(canvas.getWidth() + mDrawingRect.left - 20, position + 1, canvas.getWidth() + mDrawingRect.left, position + 1, mPaintHighlightLine);
            canvas.drawLine(canvas.getWidth() + mDrawingRect.left - 20, position + 2, canvas.getWidth() + mDrawingRect.left, position + 2, mPaintHighlightLine);
        }
    }

    /**
     * Compute the line to highlight based on selection
     */
    protected void computeLineHighlight() {
        int i, line, selStart;
        String text;

        if (!isEnabled()) {
            mHighlightedLine = -1;
            return;
        }

        selStart = getSelectionStart();
        if (mHighlightStart != selStart) {
            text = getText().toString();

            mHighlightedLine = getHighlightLine(text, selStart);
        }
    }

    /**
     * Get number of line by selection
     */
    protected int getHighlightLine(String text, int start) {
        int i, line;

        line = i = 0;
        while (i < start) {
            i = text.indexOf("\n", i);
            if (i < 0) {
                break;
            }
            if (i < start) {
                ++line;
            }
            ++i;
        }

        return line;
    }

    /**
     * @param startLineNumber real line number
     */
    public void updateLineNumber(int startLineNumber) {
        this.startLineNumber = startLineNumber;
    }


    /**
     * The set of highlight lines
     */
    private Set<Integer> mHighlightLines;

    /**
     * The line numbers paint
     */
    protected Paint mPaintHighlightLine;
    /**
     * The line numbers paint
     */
    protected Paint mPaintNumbers;
    /**
     * The line highlight paint
     */
    protected Paint mPaintHighlight;
    /**
     * the offset value in dp
     */
    protected int mPaddingDP = 6;
    /**
     * the padding scaled
     */
    protected int mPadding, mLinePadding;
    /**
     * the scale for desnity pixels
     */
    protected float mScale;

    /**
     * the scroller instance
     */
    protected Scroller mTedScroller = null;
    /**
     * the velocity tracker
     */
    protected GestureDetector mGestureDetector;
    /**
     * the Max size of the view
     */
    protected Point mMaxSize;

    /**
     * the highlighted line index
     */
    protected int mHighlightedLine;
    protected int mHighlightStart;

    /**
     * real line number shifter
     */
    protected int startLineNumber;

    protected Rect mDrawingRect, mLineBounds;
}
