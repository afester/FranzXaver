package afester.javafx.examples.board.model.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import org.junit.Test;

import afester.javafx.examples.board.tools.css.Handler;


public class StringUrlTest {

    private String getCSS(String path) {
        try {
            URL url = new URL(path);
            URLConnection con = url.openConnection();
            byte[] content = con.getInputStream().readAllBytes();
            final String res = new String(content, Charset.defaultCharset());
            return res;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Test
    public void testStringUrl() {
        Handler.registerContent("dynamicCSS", "y {x: blue; y: 1px }");
        assertEquals("y {x: blue; y: 1px }", getCSS("css:dynamicCSS"));

        Handler.registerContent("dynamicCSS",  
                "TopBoardView .TraceNormal{\r\n" + 
                "   -fx-stroke: #D2D1CF; /*#606060;*/\r\n" + 
                "   -fx-stroke-width: 0.8px;\r\n" + 
                "   -fx-stroke-line-cap: round;\r\n" + 
                "}");
        assertEquals("TopBoardView .TraceNormal{\r\n" + 
                "   -fx-stroke: #D2D1CF; /*#606060;*/\r\n" + 
                "   -fx-stroke-width: 0.8px;\r\n" + 
                "   -fx-stroke-line-cap: round;\r\n" + 
                "}", getCSS("css:dynamicCSS"));
    }
}
