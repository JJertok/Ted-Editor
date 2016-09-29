package fr.xgouchet.texteditor.syntax;

import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hightlighter {

    protected ArrayList<SyntaxToken> syntaxTokens;
    protected ArrayList<StyleToken> styleTokens;
    protected ArrayList<Object> mSpans;

    // hash map to get fast suitable style token for token 
    protected HashMap<String, StyleToken> mStyles;
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
        buildMapTokenStyle();
        buildPattern();
    }

    public Hightlighter() {
        mSpans = new ArrayList<Object>();
    }


    /**
     * Clear all styles
     */
    public void clear(Editable s) {
        for (Object span:
                mSpans) {
            s.removeSpan(span);
        }
        mSpans.clear();
    }

    /**
     *
     * @param s String which must be checked for highlighting
     */
    public void hightlight(Editable s) {
        if(mSpans == null || mStyles == null || pattern == null) return;

        clear(s);

        Matcher matcher = pattern.matcher(s.toString());

        while(matcher.find()) {
            Object span = getSpan(matcher.group());
            mSpans.add(span);
            s.setSpan(
                    span,
                    matcher.start(),
                    matcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    
    private ForegroundColorSpan getSpan(String token) {
        if(!mStyles.containsKey(token)) return null;

        StyleToken result = mStyles.get(token);
        return new ForegroundColorSpan(Color.parseColor(result.getColor()));
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
        buildMapTokenStyle();
        buildPattern();
    }

    /**
     *
     * @param styleTokens Collection of type tokens
     */
    public void setStyleTokens(ArrayList<StyleToken> styleTokens) {
        this.styleTokens = styleTokens;
        buildMapTokenStyle();
        buildPattern();
    }


    /**
     *  build hash map to get faster styletoken by token name
     */
    private void buildMapTokenStyle() {
        if(syntaxTokens == null || styleTokens == null) return;

        mStyles = new HashMap<String, StyleToken>();

        for (SyntaxToken token:
                syntaxTokens) {

            for (StyleToken style:
                    styleTokens) {
                if(style.equals(token)) {
                    mStyles.put(token.getToken(), style);
                    break;
                }
            }
        }
    }


    /**
     *  pre build regex pattern to match all tokens
     */
    private void buildPattern() {
        if(syntaxTokens == null) return;
        // build pattern to match all tokens
        StringBuilder patternBuilder = new StringBuilder();
        for (SyntaxToken token:
                syntaxTokens) {
            patternBuilder.append("(\\b" + token.getToken() + "\\b)");
            if(!token.equals(syntaxTokens.get(syntaxTokens.size() - 1))) {
                patternBuilder.append( "|");
            }
        }
        pattern = Pattern.compile(patternBuilder.toString());
    }
}
