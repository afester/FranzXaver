package afester.javafx.examples.docbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


class Handler extends DefaultHandler { //(xml.sax.handler.ContentHandler):

    
    
    private StringBuffer content = null;
    private int sectionLevel = 0;
    private DocbookHandler handler;
    private int listLevel = 0;
    private String language = "";
    private final String contentPath;

    private boolean firstPara = true;

    private TextStyle textStyle = TextStyle.EMPTY.updateWith("para", true);
    private ParStyle paraStyle = ParStyle.EMPTY;

    
    public Handler(String contentPath, DocbookHandler handler) {
        this.handler = handler;
        this.contentPath = contentPath;
    }

    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {

        // structural tags
        if (name.equals("article")) {
            // self.nodeStack = [Frame()]
        }
        else if (name.equals("section")) {
            sectionLevel += 1;
        }
        else if (name.equals("itemizedlist")) {  // create a List node and set it as current parent
            listLevel++;
            // curList = List(('itemizedlist', 'level', str(self.listLevel)))
            // parent = self.nodeStack[-1]
            // parent.add(curList)
            // self.nodeStack.append(curList)              # push
        }
        else if (name.equals("mediaobject")) {
        }
        else if (name.equals("imageobject")) {
        }
        else if (name.equals("inlineequation")) {
        }
        else if (name.equals("listitem")) {
        }

        
/***********************************/

// These contain <para> elements, but already define the paragraph style:
        // <blockquote>
        else if (name.equals("blockquote")) {
            paraStyle = ParStyle.EMPTY.updateWith("blockquote", true);
            textStyle = TextStyle.EMPTY.updateWith("blockquote", true);         // TODO: REMOVE again?
        }

        // <tip>
        else if (name.equals("tip")) {
            paraStyle = ParStyle.EMPTY.updateWith("tip", true);
            textStyle = TextStyle.EMPTY.updateWith("tip", true);         // TODO: REMOVE again?
        }

        // <blockquote>
        else if (name.equals("warning")) {
            paraStyle = ParStyle.EMPTY.updateWith("warning", true);
            textStyle = TextStyle.EMPTY.updateWith("warning", true);         // TODO: REMOVE again?
        }

/***********************************/

        // <title>
        else if (name.equals("title")) { // create a title paragraph and set it as current parent
            if (sectionLevel > 0) { // no title for <article>
                startParagraph();
                paraStyle = ParStyle.EMPTY.updateWith("h" + sectionLevel, true);
                textStyle = TextStyle.EMPTY.updateWith("h" + sectionLevel, true);         // TODO: REMOVE again?
            }
            content = new StringBuffer();
        }

        // <para>
        else if (name.equals("para")) {  // # a paragraph contains only fragments
            if (paraStyle == null) {
                paraStyle = ParStyle.EMPTY.updateWith("para", true);
                textStyle = TextStyle.EMPTY.updateWith("para", true);         // TODO: REMOVE again!
            }
            startParagraph();
            content = new StringBuffer();   // start collecting content
        }

        // <programlisting>
        else if (name.equals("programlisting")) {    //  # a program listing contains verbatim text only
            language = attributes.getValue("language");
            paraStyle = ParStyle.EMPTY.updateWith("programlisting", true).updateWith(language, true);
            textStyle = TextStyle.EMPTY.updateWith("programlisting", true).updateWith(language, true);
            startParagraph();
            
            content = new StringBuffer();   // start collecting content
        }

        // <screen>
        else if (name.equals("screen")) {    // a screen contains verbatim text only
            paraStyle = ParStyle.EMPTY.updateWith("screen", true);
            textStyle = TextStyle.EMPTY.updateWith("screen", true);
            startParagraph();
            content = new StringBuffer();   // start collecting content
        }
        
/***********************************/
        
// These are the fragments which are added to the current paragraph

        // <emphasis>
        else if (name.equals("emphasis")) {
            flushContent();

            String emphasizeRole = attributes.getValue("role");
            if (emphasizeRole != null && emphasizeRole.equals("highlight")) {
                textStyle = textStyle.updateWith("highlight",  true);
            } else {
                textStyle = textStyle.updateWith("emphasis",  true);
            }
        } 
        
        // <code>
        else if (name.equals("code")) {
            flushContent();
            textStyle = textStyle.updateWith("code", true);
        } 

        // <link>
        else if (name.equals("link")) {
            flushContent();
            textStyle = textStyle.updateWith("link", true);
            // self.href = attrs.get('xlink:href', '')
            // self.currentStyle = None            # todo: nested styles support (needs yet another stack ...)
        }

        // <olink>
        else if (name.equals("olink")) {
            flushContent();
            textStyle = textStyle.updateWith("olink", true);
        }

        // <imagedata>
        else if (name.equals("imagedata")) {
            flushContent();
            String imagePath = attributes.getValue("fileref");
            handler.addImage(contentPath + "/" + imagePath);
        }

        // <mathphrase>
        else if (name.equals("mathphrase")) {
        }
    }


    private void flushContent() {
        //if (content.length() > 0) {

        // the content might contain newlines - treat each line as a separate paragraph
        boolean first = true;
        int idx = 0;
        String[] lines = content.toString().split("\n");
        for (String line : lines) {
            System.err.printf("FLUSH: %s (%s, %s)%n", line, textStyle, paraStyle);
            handler.addFragment(line, textStyle, paraStyle, listLevel, first);
            first = false;
            if (idx < lines.length - 1 || content.toString().endsWith("\n")) {
                handler.addFragment("\n", textStyle, paraStyle, listLevel, first);    
            }
            idx++;

//            handler.addFragment("\n", textStyle, paraStyle, listLevel);
        }
//        System.err.println(Arrays.toString(lines));
//        
//        
//            System.err.printf("FLUSH: %s (%s, %s)%n", content.toString(), textStyle, paraStyle);
//            handler.addFragment(content.toString(), textStyle, paraStyle, listLevel);
            content = new StringBuffer();
        //}
    }

    private void startParagraph() {
        System.err.printf("START PARAGRAPH%n");
        if (!firstPara) {
            handler.addFragment("\n", textStyle, paraStyle, listLevel, true);
        }
        firstPara = false;
        content = new StringBuffer();
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {

        if (content != null) {
            content.append(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {

        // structural tags
        if (name.equals("article")) {
            // self.result = self.nodeStack[0]
        }
        else if (name.equals("section")) {
            sectionLevel -= 1;
        }
        else if (name.equals("itemizedlist")) {
            // self.nodeStack = self.nodeStack[0:-1]     # pop()
            content = null;
            // self.currentStyle = None
            listLevel--;
        }
        else if (name.equals("mediaobject")) {
        }
        else if (name.equals("imageobject")) {
        }
        else if (name.equals("inlineequation")) {
        }

        // These contain <para> elements:
        else if (name.equals("listitem")) {
        }
        else if (name.equals("blockquote")) {
        }
        else if (name.equals("tip")) {
        }
        else if (name.equals("warning")) {
        }

        // </title>
        else if (name.equals("title")) {
            if (sectionLevel > 0) {
                flushContent();
            }
            paraStyle = null;
            content = null;
        }

/************************************************/
        
        // </para>
        else if (name.equals("para")) {     
            flushContent();
            content = null;
            paraStyle = null;
        }
        
        // </programlisting>
        else if (name.equals("programlisting")) {
            flushContent();
            content = null;
            paraStyle = null;
        }

        // </screen>
        else if (name.equals("screen")) {
            flushContent();
            content = null;
            paraStyle = null;
        }
        
/************************************************/        
        // </emphasis>
        else if (name.equals("emphasis")) {
            //parent = self.nodeStack[-1]

//            else if (name.equals("highlight")) {
//                handler.addFragment(content.toString(), textStyle);
//                content = new StringBuffer();
//                textStyle = textStyle.updateWith("highlight", false);
//            }

            flushContent();
//            handler.addFragment(content.toString(), textStyle);
//            //frag = TextFragment(self.currentStyle)
//            //frag.setText(self.content)
//            content = new StringBuffer();
            //parent.add(frag)
            //self.currentStyle = None                # todo: nested styles support (needs yet another stack ...)
            textStyle = textStyle.updateWith("emphasis", false);
            textStyle = textStyle.updateWith("highlight", false);   // TODO: Nested styles!
        }

        // </code>
        else if (name.equals("code")) { // inline code
            flushContent();
            textStyle = textStyle.updateWith("code", false);
        }

        // </link>
        else if (name.equals("link")) {
            //parent = self.nodeStack[-1]

            //frag = TextFragment(self.currentStyle)
            //frag.setText(self.content)
            //frag.setHref(self.href)
            //self.href = None
            //self.content = ''
            //parent.add(frag)
            //self.currentStyle = None                # todo: nested styles support (needs yet another stack ...)
//            handler.addFragment(content.toString(), textStyle);
//            content = new StringBuffer();
            flushContent();
            textStyle = textStyle.updateWith("link", false);
        }
        
        // </olink>
        else if (name.equals("olink")) {
            //self.keywordLinks.add(self.content) 

            flushContent();
            textStyle = textStyle.updateWith("olink", false);
        }
        else if (name.equals("imagedata")) {
        }
        else if (name.equals("mathphrase")) {
            //self.currentStyle = None                # todo: nested styles support (needs yet another stack ...)

            handler.addFormula(content.toString());
            content = new StringBuffer();
        }
    }

}


public class DocbookImporter {
    
    private final String contentPath;
    private final String contentFile;
    private DocbookHandler docHandler;

    /**
     * 
     * @param contentPath The path to the docbook xml file
     * @param contentFile The name of the docbook xml file to import
     */
    public DocbookImporter(String contentPath, String contentFile) { // , formatManager):
        this.contentPath = contentPath;
        this.contentFile = contentFile;
        // this.formatManager = formatManager
    }

    public void importDocument(DocbookHandler docbookHandler) {
        this.docHandler = docbookHandler;
        String contentFilePath = Paths.get(contentPath, contentFile).toString();
        try (InputStream is = new FileInputStream(contentFilePath)) {   // TODO: Encoding??
            importFromFile(is);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importFromFile(InputStream is, DocbookHandler handler) {
        this.docHandler = handler;
        importFromFile(is);
    }

    private void importFromFile(InputStream is) {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = parserFactory.newSAXParser();
            System.err.println("Using " + parser.getClass());
            DefaultHandler handler = new Handler(contentPath, docHandler);
            parser.parse(is, handler);
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
