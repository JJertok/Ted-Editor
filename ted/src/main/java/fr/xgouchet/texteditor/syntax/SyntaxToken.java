package fr.xgouchet.texteditor.syntax;


public class SyntaxToken {
    protected String token;
    protected String styleName;

    /**
     *
     * @param token Syntax item
     * @param styleName Style name for this token
     */
    SyntaxToken(String token, String styleName) {
        this.token = token;
        this.styleName = styleName;
    }

    public String getToken() {
        return token;
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
            return styleName.equals(((StyleToken) obj).token);
        }
        return obj instanceof SyntaxToken &&
                token.equals(((SyntaxToken) obj).token)&&
                styleName.equals(((SyntaxToken) obj).styleName);
    }
}
