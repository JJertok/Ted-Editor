package fr.xgouchet.texteditor.syntax;


public class SyntaxToken {
    protected String token;
    protected String styleToken;

    /**
     *
     * @param token Syntax item
     * @param styleToken Style token for this token
     */
    SyntaxToken(String token, String styleToken) {
        this.token = token;
        this.styleToken = styleToken;
    }

    public String getToken() {
        return token;
    }

    public String getStyleToken() {
        return styleToken;
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
            return styleToken.equals(((StyleToken) obj).token);
        }
        return obj instanceof SyntaxToken &&
                token.equals(((SyntaxToken) obj).token)&&
                styleToken.equals(((SyntaxToken) obj).styleToken);
    }
}
