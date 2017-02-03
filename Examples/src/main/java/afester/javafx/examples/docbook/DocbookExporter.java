package afester.javafx.examples.docbook;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

import org.fxmisc.richtext.model.ListItem;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyledDocument;
import org.fxmisc.richtext.model.StyledText;
import org.reactfx.util.Either;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import afester.javafx.examples.docbook.util.Escaper;
import afester.javafx.examples.docbook.util.PercentEscaper;

public class DocbookExporter {
    private PrintWriter out;
    private Element articleElement;
    private Document xmlDoc; 
    private LinkedList<Element> sections = new LinkedList<>();
    private LinkedList<Element> lists = new LinkedList<>();
    private String pathPrefix;

    public DocbookExporter(OutputStream os, String pathPrefix, String string2) {
        out = new PrintWriter(os);
        this.pathPrefix = pathPrefix; 
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

        Element articleTitleElement = xmlDoc.createElement("title");
        articleElement.appendChild(articleTitleElement);

        addDocumentElements(doc);

        DocbookWriter tf = new DocbookWriter();
        
//        Transformer tf = null;
//        try {
//            tf = TransformerFactory.newInstance().newTransformer();
//        } catch (TransformerConfigurationException e) {
//            e.printStackTrace();
//        } catch (TransformerFactoryConfigurationError e) {
//            e.printStackTrace();
//        }
//        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//        tf.setOutputProperty(OutputKeys.INDENT, "yes");
//        tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
//        try {
        System.err.println("OUT: " + out);
        tf.transform(new DOMSource(xmlDoc), new StreamResult(out));
//        } catch (TransformerException e) {
//            e.printStackTrace();
//        }

    }
    
    void addDocumentElements(StyledDocument<ParStyle, Either<StyledText<TextStyle>, CustomObject<TextStyle>>, TextStyle> doc) {

        for (Paragraph<ParStyle, Either<StyledText<TextStyle>, CustomObject<TextStyle>>, TextStyle> p : doc.getParagraphs()) {
            ParStyle ps = p.getParagraphStyle();
            List<String> styles = ps.getStyles();

//            System.err.println(styles + "/" + p.getListItem());

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
            } else {

//****************************************************************

                // check for List
                int curListLevel = lists.size();
                int pListLevel = p.getListItem().orElse(new ListItem(0, false)).getLevel();
                System.err.printf("**LIST: %s - %s%n",  curListLevel, pListLevel);

                // parent is either an itemizedlist or the current section
                Element parent = lists.peek();
                if (parent == null) {
                    parent = sections.peek();
                }

                // create required itemizedlist levels
                while(lists.size() < pListLevel) { //  - 1) {
                    Element newList = xmlDoc.createElement("itemizedlist");
                    lists.push(newList);

                    parent.appendChild(newList);
                    parent = newList;
                }

                // go back to proper itemizedlist level
                while(lists.size() > pListLevel) {
                    lists.pop();
                }

                parent = lists.peek();
                if (parent != null) {
                    Element listItemElement = xmlDoc.createElement("listitem");
                    parent.appendChild(listItemElement);
                    parent = listItemElement;
                } else {
                    parent = sections.peek();
                }

//****************************************************************
                if (styles.size() > 0 && styles.get(0).equals("screen")) {
                    Element screenElement = xmlDoc.createElement("screen");
                    parent.appendChild(screenElement);
    
                    exportSegments(p, screenElement);
                } else if (styles.size() > 0 && styles.get(0).equals("programlisting")) {
                    String language = styles.get(1);
    
                    Element pgmListingElement = xmlDoc.createElement("programlisting");
                    pgmListingElement.setAttribute("language", language);
                    parent.appendChild(pgmListingElement);
    
                    exportSegments(p, pgmListingElement);
                } else if (styles.contains("warning")) {
                    Element warningElement = xmlDoc.createElement("warning");
                    Element paraElement = xmlDoc.createElement("para");
                    parent.appendChild(warningElement);
                    warningElement.appendChild(paraElement);
    
                    exportSegments(p, paraElement);
                } else if (styles.contains("tip")) {
                    Element tipElement = xmlDoc.createElement("tip");
                    Element paraElement = xmlDoc.createElement("para");
                    parent.appendChild(tipElement);
                    tipElement.appendChild(paraElement);
    
                    exportSegments(p, paraElement);
                } else  if (styles.contains("blockquote")) {
                    Element blockquoteElement = xmlDoc.createElement("blockquote");
                    Element paraElement = xmlDoc.createElement("para");
                    parent.appendChild(blockquoteElement);
                    blockquoteElement.appendChild(paraElement);
    
                    exportSegments(p, paraElement);
                } else {
                    Element paraElement = xmlDoc.createElement("para");
                    parent.appendChild(paraElement);
    
                    exportSegments(p, paraElement);
                }
            }
        }
    }

    
    private static final String URL_PATH_OTHER_SAFE_CHARS_LACKING_PLUS =
                   "-._~" +        // Unreserved characters.
                   "!$'()*,;&=" +  // The subdelim characters (excluding '+').
                   "@:";       
    private void exportSegments(Paragraph<ParStyle, Either<StyledText<TextStyle>, CustomObject<TextStyle>>, TextStyle> p, Element parentElement) {
        for (Either<StyledText<TextStyle>, CustomObject<TextStyle>> segment : p.getSegments()) {

            

            if (segment.isLeft()) { // Text segment
                StyledText<TextStyle> seg = segment.getLeft();
                TextStyle segStyle = seg.getStyle();

                if (segStyle.contains("emphasis")) {
                    Element segmentElement = xmlDoc.createElement("emphasis");
                    Node text = xmlDoc.createTextNode(seg.getText());
                    segmentElement.appendChild(text);
                    parentElement.appendChild(segmentElement);
                } else if (segStyle.contains("highlight")) {
                    Element segmentElement = xmlDoc.createElement("highlight");
                    Node text = xmlDoc.createTextNode(seg.getText());
                    segmentElement.appendChild(text);
                    parentElement.appendChild(segmentElement);
                } else if (segStyle.contains("code")) {
                    Element segmentElement = xmlDoc.createElement("code");
                    Node text = xmlDoc.createTextNode(seg.getText());
                    segmentElement.appendChild(text);
                    parentElement.appendChild(segmentElement);
                } else if (segStyle.contains("olink")) {
                    Element segmentElement = xmlDoc.createElement("olink");
//                    String ref = "";
//                    try {
                        Escaper escaper = new PercentEscaper(URL_PATH_OTHER_SAFE_CHARS_LACKING_PLUS + "+/?", false);
                        String ref = escaper.escape(seg.getText());
//
//                        // ref = URLEncoder.encode(seg.getText(), "UTF-8"); // does not encode space character as expected
//                        ref = seg.getText();
//                        ref = ref.replaceAll(" ", "%20");   // TODO: Check
//                        ref = URLEncoder.encode(ref, "UTF-8"); // does not encode space character as expected
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    } 
                    segmentElement.setAttribute("targetdoc", ref);
                    Node text = xmlDoc.createTextNode(seg.getText());
                    segmentElement.appendChild(text);
                    parentElement.appendChild(segmentElement);
                } else {
                    Node text = xmlDoc.createTextNode(seg.getText());
                    parentElement.appendChild(text);
                }

            } else {                // custom object segment
                CustomObject<TextStyle> seg = segment.getRight();
                if (seg instanceof LinkedImage) {
                    Element mediaElement = xmlDoc.createElement("mediaobject");
                    Element imageElement = xmlDoc.createElement("imageobject");
                    Element imageDataElement = xmlDoc.createElement("imagedata");
                    imageDataElement.setAttribute("fileref", ((LinkedImage) seg).getImageFile());   // TODO: Check image path / filename 
                    imageElement.appendChild(imageDataElement);
                    mediaElement.appendChild(imageElement);

                    parentElement.appendChild(mediaElement);
                } else if (seg instanceof LatexFormula) {
                    Element equationElement = xmlDoc.createElement("inlineequation");
                    Element phraseElement = xmlDoc.createElement("mathphrase");
                    Node text = xmlDoc.createTextNode( ((LatexFormula) seg).getFormula());
                    equationElement.appendChild(phraseElement);
                    phraseElement.appendChild(text);

                    parentElement.appendChild(equationElement);
                } else {
                    throw new RuntimeException("EXPORT ERROR: INVALID SEGMENT " + seg);
                }
            }
        }
    }
}
