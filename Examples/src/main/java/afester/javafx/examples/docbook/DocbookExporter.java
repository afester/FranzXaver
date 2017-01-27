package afester.javafx.examples.docbook;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyledDocument;
import org.fxmisc.richtext.model.StyledText;
import org.reactfx.util.Either;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DocbookExporter {
    private PrintWriter out;
    private Element articleElement;
    private Document xmlDoc; 
    private LinkedList<Element> sections = new LinkedList<>();
    
    
    public DocbookExporter(OutputStream os, String string, String string2) {
        out = new PrintWriter(os);
        
    }

    // @TODO
    private String indent(int level) {
        return "                                    ".substring(0, level*2);
    }


    public void exportToFile(StyledDocument<ParStyle, Either<StyledText<TextStyle>, CustomObject<TextStyle>>, TextStyle> doc) {
        
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        xmlDoc = builder.newDocument();

        articleElement = xmlDoc.createElement("article");
        articleElement.setAttribute("version", "5.0");
        articleElement.setAttribute("xml:lang", "en");
        articleElement.setAttribute("xmlns", "http://docbook.org/ns/docbook");
        articleElement.setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
        xmlDoc.appendChild(articleElement);

        addDocumentElements(doc);

        Transformer tf = null;
        try {
            tf = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            e.printStackTrace();
        }
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        try {
            tf.transform(new DOMSource(xmlDoc), new StreamResult(out));
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }
    
    void addDocumentElements(StyledDocument<ParStyle, Either<StyledText<TextStyle>, CustomObject<TextStyle>>, TextStyle> doc) {

        for (Paragraph<ParStyle, Either<StyledText<TextStyle>, CustomObject<TextStyle>>, TextStyle> p : doc.getParagraphs()) {
            ParStyle ps = p.getParagraphStyle();
            List<String> styles = ps.getStyles();

            System.err.println(styles + "/" + p.getListItem());
            // handle styles
            Optional<String> headerStyle = styles.stream().filter(s -> s.startsWith("h")).findFirst();
            if (headerStyle.isPresent()) {
                int newLevel = Integer.parseInt(headerStyle.get().substring(1));

                while(sections.size() < newLevel - 1) {
                    Element newSection = xmlDoc.createElement("section");
                    Element sectionTitle = xmlDoc.createElement("title");
                    newSection.appendChild(sectionTitle);

                    articleElement.appendChild(newSection);
                    sections.push(newSection);

                }

                while(sections.size() >= newLevel) {
                    sections.pop();
                }

                Element newSection = xmlDoc.createElement("section");
                Element sectionTitle = xmlDoc.createElement("title");
                Node titleText = xmlDoc.createTextNode(p.getText());
                newSection.appendChild(sectionTitle);
                sectionTitle.appendChild(titleText);

                if (sections.peek() == null) {
                    articleElement.appendChild(newSection);
                } else {
                    sections.peek().appendChild(newSection);
                }
                sections.push(newSection);
            } else if (styles.size() > 0 && styles.get(0).equals("programlisting")) {
                String language = styles.get(1);

                Element section = sections.peek();
                Element pgmListingElement = xmlDoc.createElement("programlisting");
                pgmListingElement.setAttribute("language", language);
                section.appendChild(pgmListingElement);

                exportSegments(p, pgmListingElement);
            } else if (styles.contains("tip")) {
                Element section = sections.peek();
                Element tipElement = xmlDoc.createElement("tip");
                Element paraElement = xmlDoc.createElement("para");
                section.appendChild(tipElement);
                tipElement.appendChild(paraElement);

                exportSegments(p, tipElement);
            } else  if (styles.contains("blockquote")) {
                Element section = sections.peek();
                Element blockquoteElement = xmlDoc.createElement("blockquote");
                Element paraElement = xmlDoc.createElement("para");
                section.appendChild(blockquoteElement);
                blockquoteElement.appendChild(paraElement);

                exportSegments(p, blockquoteElement);
            } else {
                Element section = sections.peek();
                Element paraElement = xmlDoc.createElement("para");
                section.appendChild(paraElement);

                exportSegments(p, paraElement);
            }
        }
    }

    private void exportSegments(Paragraph<ParStyle, Either<StyledText<TextStyle>, CustomObject<TextStyle>>, TextStyle> p, Element parentElement) {
        for (Either<StyledText<TextStyle>, CustomObject<TextStyle>> segment : p.getSegments()) {
            Element segmentElement = xmlDoc.createElement("segment");

            if (segment.isLeft()) {
                StyledText<TextStyle> seg = segment.getLeft();
                Node text = xmlDoc.createTextNode(seg.getText());
                segmentElement.appendChild(text);
                
            } else {
                CustomObject<TextStyle> seg = segment.getRight();
                Node text = xmlDoc.createTextNode(seg.toString());
                segmentElement.appendChild(text);
            }
            
            parentElement.appendChild(segmentElement);
        }
    }
}
