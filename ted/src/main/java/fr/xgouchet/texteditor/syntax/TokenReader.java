package fr.xgouchet.texteditor.syntax;

import android.util.Xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class TokenReader {
    private static final String ns = null;

    /**
     * @param in Input Stream with xml data
     * @return XmlParser
     * @throws XmlPullParserException
     * @throws IOException
     */
    private XmlPullParser createParser(InputStream in) throws XmlPullParserException, IOException {

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return parser;
        } finally {
            in.close();
        }

    }

    /**
     * @param parser
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    /**
     *
     * @param parser XmlParser with current Token
     * @return Object of syntax token
     * @throws XmlPullParserException
     * @throws IOException
     */
    private SyntaxToken readSyntaxToken(XmlPullParser parser) throws XmlPullParserException, IOException {
        String token = parser.getAttributeValue(null, "token");
        String styleToken = parser.getAttributeValue(null, "styleToken");
        return new SyntaxToken(token, styleToken);
    }

    /**
     * @param in Input Stream, which contains Syntax Tokens of some programming language in xml format.
     * @return Collection of Syntax Tokens
     */
    public ArrayList<SyntaxToken> readSyntaxTokens(InputStream in, String syntaxBlock) throws XmlPullParserException, IOException {

        ArrayList<SyntaxToken> syntaxTokens = new ArrayList<SyntaxToken>();
        XmlPullParser parser = createParser(in);

        parser.require(XmlPullParser.START_TAG, ns, "SyntaxTokens");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            if (parser.getName().equals(syntaxBlock)) {
                parser.require(XmlPullParser.START_TAG, ns, syntaxBlock);
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) continue;

                    String name = parser.getName();
                    if (name.equals("token")) {
                        syntaxTokens.add(readSyntaxToken(parser));
                        parser.next();
                    } else skip(parser);
                }
            }else skip(parser);
        }
        return syntaxTokens;
    }

    /**
     *
     * @param parser XmlParser with current Token
     * @return Style Token object
     * @throws XmlPullParserException
     * @throws IOException
     */
    public StyleToken readStyleToken(XmlPullParser parser) throws XmlPullParserException, IOException {

        String token = parser.getAttributeValue(null, "token");
        String color = parser.getAttributeValue(null, "color");
        Boolean bold = Boolean.parseBoolean(parser.getAttributeValue(null, "bold"));
        Boolean italic = Boolean.parseBoolean(parser.getAttributeValue(null, "italic"));

        return new StyleToken(token, color, bold, italic);
    }

    /**
     * @param in Input Stream, which contains Style Tokens with information about the token
     *           style(name) and color of highlighting.
     * @return Collection of Style Tokens
     */
    public ArrayList<StyleToken> readStyleTokens(InputStream in, String styleBlock) throws IOException, XmlPullParserException {

        ArrayList<StyleToken> styleTokens = new ArrayList<StyleToken>();
        XmlPullParser parser = createParser(in);

        parser.require(XmlPullParser.START_TAG, ns, "StyleTokens");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            if (parser.getName().equals(styleBlock)) {
                parser.require(XmlPullParser.START_TAG, ns, styleBlock);
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) continue;

                    String name = parser.getName();
                    if (name.equals("style")) {
                        styleTokens.add(readStyleToken(parser));
                        parser.next();
                    } else skip(parser);
                }
            }else skip(parser);
        }
        return styleTokens;
    }
}
