package afester.javafx.examples.board.tools.css;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Source: https://stackoverflow.com/a/39071332/1611055
 *
 * I abhor the name of this class,
 * but it must be called "Handler" in order for java.net.URL to be able to find us.
 *
 * It sucks, but it's not our api, and it's the only way to get dynamic stylesheets in JavaFx,
 * short of overriding the url stream handler directly (and this can only be done once in a single
 * JVM, and as framework-level code, it is unacceptable to prevent clients from choosing to
 * override the stream handler themselves).
 *
 * Created by James X. Nelson (james @wetheinter.net) on 8/21/16.
 * 
 * Usage: 
 *   1. Register CSS code for path: Handler.registerStylesheet("my/path", () -> "* { -fx-css: blah }");
 *   2. Use the CSS "file" via "css:my/path" URL.
 */
public class Handler extends URLStreamHandler {

    private static final Map<String, String> content;

    static {
        // Ensure that we are registered as a url protocol handler for css:/path css files.
        // The last element of the package name specifies the protocol name!
        String was = System.getProperty("java.protocol.handler.pkgs", "");
        System.setProperty("java.protocol.handler.pkgs", Handler.class.getPackage().getName().replace(".css", "") +
            (was.isEmpty() ? "" : "|" + was ));

        content = new HashMap<>();
    }

    /**
     * Registers some String content for a given path.
     *
     * @param path The path name for the content.
     * @param contents The content itself.
     */
    public static void registerContent(String path, String contents) {
        content.put(path, contents);
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        final String path = u.getPath();
        final String file = content.get(path);
        return new StringURLConnection(u, file);
    }


    private static class StringURLConnection extends URLConnection {
        private final String contents;

        public StringURLConnection(URL url, String contents){
            super(url);
            this.contents = contents;
        }

        @Override
        public void connect() throws IOException {}

        @Override 
        public InputStream getInputStream() throws IOException {
            final var buf = contents.getBytes(Charset.defaultCharset());  
            return new ByteArrayInputStream(buf);
        }
    }
}
