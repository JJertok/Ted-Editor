package fr.xgouchet.texteditor.syntax;

public class StyleToken {
    protected String styleName;
    protected String color;
    protected Boolean bold;
    protected Boolean italic;

    /**
     *
     * @param styleName Style Token name
     * @param color Color in hex format without #
     * @param bold Flag which shows that text must be bold
     * @param italic Flag which shows that text must be italic
     */

    StyleToken(String styleName, String color, Boolean bold, Boolean italic) {
        this.styleName = styleName;
        this.color = color;
        this.bold = bold;
        this.italic = italic;
    }

    public String getStyleName() {
        return styleName;
    }

    public String getColor() {
        return color;
    }

    public Boolean getBold() {
        return bold;
    }

    public Boolean getItalic() {
        return italic;
    }

    /**
     * Operation have two paths one for comparing with Syntax Token
     * for checking style names of Syntax Tokens.
     * Other one for compering Style Tokens
     * @param obj Object with which we compare current class
     * @return Result of comparison operation
     */


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SyntaxToken) {
            return styleName.equals(((SyntaxToken) obj).styleName);
        }
        return obj instanceof StyleToken &&
                styleName.equals(((StyleToken) obj).styleName) &&
                color.equals(((StyleToken) obj).color) &&
                bold.equals(((StyleToken) obj).bold) &&
                italic.equals(((StyleToken) obj).italic);
    }
}
