package afester.javafx.examples.docbook;


import java.io.PrintWriter;
import java.io.Writer;
import java.security.InvalidParameterException;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * A custom transformer to transform the DocBook DOM tree
 * into a format which is identical to the one used for the current
 * Python based implementation.
 * Essentially used to make verification of the new implementation easier. 
 *
 */
public class DocbookWriter {

    private PrintWriter out;
    

    public void transform(DOMSource domSource, StreamResult streamResult) {
        Writer dest = streamResult.getWriter();    //  . getOutputStream(); // TODO: OutputStream is probably better
        if (dest == null) {
            throw new InvalidParameterException("Destination Stream can not be null!");
        }
        out = new PrintWriter(dest);
//        out = System.err;

        Document doc = (Document) domSource.getNode();
        out.printf("<?xml version=\"1.0\" encoding=\"utf-8\"?>",
                    doc.getXmlVersion(), "UTF-8");

        Node rootElement = doc.getFirstChild();
        exportDOM(rootElement, 0);

        out.println();
        out.flush();
    }

    private void exportDOM(Node parent, int indent) {
        String element = parent.getNodeName();

        if ("section".equals(element)) {
            out.printf("%n%n%s<%s", prefix(indent), element);
        } else if ("inlineequation".equals(element) || 
                   "code".equals(element) || "emphasis".equals(element) || "highlight".equals(element) || 
                   "mediaobject".equals(element) || "imagedata".equals(element) || "imageobject".equals(element) || 
                   "mathphrase".equals(element) || "olink".equals(element) ||
                   "blockquote".equals(parent.getParentNode().getNodeName()) ||
                   "warning".equals(parent.getParentNode().getNodeName()) ||
                   "tip".equals(parent.getParentNode().getNodeName()) ) {
            out.printf("<%s", element);
        } else {
            out.printf("%n%s<%s", prefix(indent), element);
        }

//***************************************************
        NamedNodeMap attrs = parent.getAttributes();
        for (int idx = 0;  attrs != null && idx < attrs.getLength();  idx++) {
            Node attr = attrs.item(idx);
            String attrName = attr.getNodeName();
            String attrValue = attr.getNodeValue();
            if ("xmlns".equals(attrName) || "xmlns:xlink".equals(attrName)) {
                out.printf("%n         %s=\"%s\"", attrName, attrValue);
            } else {
                out.printf(" %s=\"%s\"", attrName, attrValue);
            }
        }
//***************************************************

        NodeList children = parent.getChildNodes();
        if (children.getLength() > 0) {
            out.printf(">");
            for (int idx = 0;  idx < children.getLength();  idx++) {
                Node child = children.item(idx);
                if (child.getNodeType() == Node.TEXT_NODE) {
                    out.printf(escape(child.getTextContent()));
                } else {
                    exportDOM(child, indent + 2);
                }
            }
            
            if ("code".equals(element) || "emphasis".equals(element) || "highlight".equals(element) || "inlineequation".equals(element) || 
                "mathphrase".equals(element) || "title".equals(element) || "para".equals(element) || "olink".equals(element) ||
                "blockquote".equals(element) || "warning".equals(element) || "tip".equals(element) ||
                "mediaobject".equals(element) || "imageobject".equals(element) || "programlisting".equals(element) ||
                "screen".equals(element)) {
                out.printf("</%s>", element);
                
            } else {
                out.printf("%n%s</%s>", prefix(indent), element);
            }
        } else {
            out.printf("/>");
        }
    }

    private String prefix(int indent) {
        return "                                                                      ".substring(0,  indent);
    }

    
    private static String escape(String originalUnprotectedString) {
        if (originalUnprotectedString == null) {
            return null;
        }

        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < originalUnprotectedString.length(); i++) {
            char ch = originalUnprotectedString.charAt(i);

            String escaped = getEntityRef(ch);
            if (escaped != null) {
                stringBuffer.append("&" + escaped + ";");
            } else {
                stringBuffer.append(ch);
            }
        }

        return stringBuffer.toString();
    }

    
    protected static String getEntityRef( int ch ) {
        // Encode special XML characters into the equivalent character references.
        // These five are defined by default for all XML documents.
        switch (ch) {
        case '<':
            return "lt";
        case '>':
            return "gt";
//        case '"':
//            return "quot";
//        case '\'':
//            return "apos";
        case '&':
            return "amp";
        }
        return null;
    }

}
