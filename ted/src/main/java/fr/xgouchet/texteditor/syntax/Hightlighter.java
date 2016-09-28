package fr.xgouchet.texteditor.syntax;

import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hightlighter {

    protected ArrayList<SyntaxToken> syntaxTokens;
    protected ArrayList<StyleToken> styleTokens;


    protected ArrayList<Object> mSpans;

    protected Pattern pattern;
    /**
     *
     * @param st Collection of syntax tokens
     * @param tt Collection of type tokens
     */
    public Hightlighter(ArrayList<SyntaxToken> st, ArrayList<StyleToken> tt) {
        syntaxTokens = st;
        styleTokens = tt;

        mSpans = new ArrayList<Object>();

        // build pattern to match all tokens
        StringBuilder patternBuilder = new StringBuilder();
        patternBuilder.append("(");
        for (SyntaxToken token:
                syntaxTokens) {
            patternBuilder.append(token.getToken());
            if(!token.equals(syntaxTokens.get(syntaxTokens.size() - 1))) {
                patternBuilder.append( "|");
            }
        }
        patternBuilder.append(")");

        pattern = Pattern.compile(patternBuilder.toString());
    }

    /**
     *
     * @param s String which must be checked for highlighting
     */
    public void hightlight(CharSequence s) {

    }


    /**
     *
     * @param s String which must be checked for highlighting
     */
    public void hightlight(Editable s) {
        for (Object span:
                mSpans) {
            s.removeSpan(span);
        }

        Matcher matcher = pattern.matcher(s.toString());
        while(matcher.find()) {
            String hexColor = "#00FF00";

            Object span = new ForegroundColorSpan(Color.parseColor(hexColor));
            mSpans.add(span);
            s.setSpan(
                    span,
                    matcher.start(),
                    matcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    /**
     *
     * @param s String which must be checked for highlighting
     * @param start Start of substring for checking
     * @param end End of substring for checking
     */
    public void hightlight(CharSequence s, int start, int end) {

    }

    /**
     *
     * @param syntaxTokens Collection of syntax tokens
     */
    public void setSyntaxTokens(ArrayList<SyntaxToken> syntaxTokens) {
        this.syntaxTokens = syntaxTokens;
    }

    /**
     *
     * @param styleTokens Collection of type tokens
     */
    public void setStyleTokens(ArrayList<StyleToken> styleTokens) {
        this.styleTokens = styleTokens;
    }
}
