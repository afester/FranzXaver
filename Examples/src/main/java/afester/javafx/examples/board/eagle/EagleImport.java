package afester.javafx.examples.board.eagle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import afester.javafx.examples.board.model.Board;
import afester.javafx.examples.board.model.Net;
import afester.javafx.examples.board.model.NetImport;
import afester.javafx.examples.board.model.Part;
import javafx.geometry.Point2D;


public class EagleImport extends NetImport {

    private File schematicFile = null;

    public EagleImport(File file) {
        schematicFile = file;
    }

    @Override
	public void importFile(Board board) {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setValidating(false);
        spf.setNamespaceAware(true);

        try {
            System.err.printf("IMPORT: %s\n", schematicFile);

            spf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            SAXParser saxParser = spf.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            EagleSchematicHandler handler = new EagleSchematicHandler();
            xmlReader.setContentHandler(handler);
            InputStream is = new FileInputStream(schematicFile);
            xmlReader.parse(new InputSource(is));

            Collection<Part> parts = handler.getParts();
            System.err.printf("Imported %s parts:\n", parts.size());
            parts.forEach(p -> System.err.println("    " + p));
            parts.forEach(p -> board.addPart(p));

            List<Net> nets = handler.getNets();
            System.err.printf("Imported %s nets:\n", nets.size());
            nets.forEach(net -> System.err.println("    " + net));
            nets.forEach(net -> board.addNet(net));

            // move all parts to "positive" Y coordinates
            final Point2D delta = new Point2D(0, -handler.minY);
            board.getParts().values().forEach(part -> {
                part.setPosition(part.getPosition().add(delta));
            });

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        

    }
}
