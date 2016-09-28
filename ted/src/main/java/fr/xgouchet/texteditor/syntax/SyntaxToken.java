package fr.xgouchet.texteditor.syntax;


public class SyntaxToken {
    protected String word;
    protected String styleName;

    /**
     *
     * @param word Syntax item
     * @param styleName Style name for this token
     */
    SyntaxToken(String word, String styleName) {
        this.word = word;
        this.styleName = styleName;
    }

    public String getWord() {
        return word;
    }

    public String getStyleName() {
        return styleName;
    }

    /**
     * Operation have two paths one for comparing Syntax Tokens
     * Other one for compering Style Tokens names
     * @param obj Object with which we compare current class
     * @return Result of comparison operation
     *
     */



    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StyleToken ) {
            return styleName.equals(((StyleToken) obj).styleName);
        }
        return obj instanceof SyntaxToken &&
                word.equals(((SyntaxToken) obj).word)&&
                styleName.equals(((SyntaxToken) obj).styleName);
    }
}
