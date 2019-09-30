package afester.javafx.svg;

import org.apache.batik.css.parser.Parser;
import org.w3c.css.sac.CSSException;
import java.io.IOException;

public class InkscapeCssParser extends Parser {
    public void parseStyleDeclaration(String source)
            throws CSSException, IOException {
        source = source.replaceAll(";-inkscape", ";inkscape");
        super.parseStyleDeclaration(source);
    }
}