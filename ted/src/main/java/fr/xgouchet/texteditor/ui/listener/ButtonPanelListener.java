package fr.xgouchet.texteditor.ui.listener;

import android.view.MotionEvent;
import android.view.View;
import fr.xgouchet.texteditor.ui.view.AdvancedEditText;


public class ButtonPanelListener implements View.OnTouchListener {
    public ButtonPanelListener(int buttomNumber, AdvancedEditText editor){
        mCenterSymb = symbols[buttomNumber-1][0];
        mLeftTopSymb = symbols[buttomNumber-1][1];
        mRightTopSymb = symbols[buttomNumber-1][2];
        mLeftBottomSymb = symbols[buttomNumber-1][3];
        mRightBottomSymb = symbols[buttomNumber-1][4];
        mText = editor;
    }

    private void addSymbol(float dx, float dy){
        if (dx == 0.0 && dx == dy)
            mText.getText().insert(mText.getSelectionStart(), mCenterSymb);
        else {
            if (dx >= 0.1 && dy >= 0.1) {
                mText.getText().insert(mText.getSelectionStart(), mRightBottomSymb);

            } else if (dx >= 0.1 && dy <= -0.1) {
                mText.getText().insert(mText.getSelectionStart(), mRightTopSymb);

            } else if (dx <= -0.1 && dy >= 0.1) {
                mText.getText().insert(mText.getSelectionStart(), mLeftBottomSymb);

            } else if (dx <= -0.1 && dy <= -0.1) {
                mText.getText().insert(mText.getSelectionStart(),mLeftTopSymb );
            }
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsDown = true;
                mPreviousX = x;
                mPreviousY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                // nothing to do
                break;
            case MotionEvent.ACTION_UP:
                if (mIsDown) {
                    float dx = x - mPreviousX;
                    float dy = y - mPreviousY;

                    addSymbol(dx,dy);

                }
                mIsDown = false;
                break;
            default:
                mIsDown = false;
                break;
        }
        return false;
    }

    /** Symbols on buttons. The button number = row + 1
     * location of the button by numbers of massive elements:
        * 0 - center
        * 1 - top left
        * 2 - top right
        * 3 - bottom left
        * 4 - bottom right */
    protected static CharSequence[][] symbols=
            {{"0","1","2","3","4"},
                    {"5","6","7","8","9"},
                    {"\'","[","]","(",")"},
                    {"\"","<",">","{","}"},
                    {";",":","_",".",","},
                    {"=","+","-","*","^"},
                    {"&","\\","/","|","~"},
                    {"!","?","%","#","@"}};

    //symbols on button by location
    protected CharSequence mCenterSymb;
    protected CharSequence mLeftTopSymb;
    protected CharSequence mRightTopSymb;
    protected CharSequence mLeftBottomSymb;
    protected CharSequence mRightBottomSymb;

    //our text editor
    protected AdvancedEditText mText;

    protected boolean mIsDown;

    //first coordinates of touching
    protected float mPreviousX;
    protected float mPreviousY;
}
