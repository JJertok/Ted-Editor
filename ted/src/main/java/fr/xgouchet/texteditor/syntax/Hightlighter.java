package fr.xgouchet.texteditor.syntax;

import java.util.ArrayList;

public class Hightlighter {

    protected ArrayList<SyntaxToken> syntaxTokens;
    protected ArrayList<StyleToken> styleTokens;


    /**
     *
     * @param st Collection of syntax tokens
     * @param tt Collection of type tokens
     */
    Hightlighter(ArrayList<SyntaxToken> st, ArrayList<StyleToken> tt) {
       syntaxTokens = st;
        styleTokens = tt;
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
