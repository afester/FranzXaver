package afester.javafx.examples.text;

import java.net.URL;

import afester.javafx.components.Meter;
import afester.javafx.examples.animation.counter.AnimatedCounter;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class RichTextAreaSkin extends SkinBase<RichTextArea> {

    // the main layout node. Contains all paragraphs layouted vertically.
    private ParagraphLayout paragraphs;
    private RichTextArea control;

    protected RichTextAreaSkin(RichTextArea control) {
        super(control);

        this.control = control;     // TODO: correct pattern???????

        Caret caret = new Caret();

        Polygon selection = new Polygon();
        selection.setFill(new Color(0x3f/255.0, 0xa8/255.0, 0xff/255.0, 1.0));
        selection.setManaged(false);
        selection.getPoints().addAll(new Double[] { 20.0,  0.0,
                                                   120.0,  0.0,
                                                   120.0, 80.0,
                                                    10.0, 80.0,
                                                    10.0, 100.0,
                                                     0.0, 100.0, 
                                                     0.0, 40.0,
                                                    20.0, 40.0});
/*
        ParagraphNode p1 = new ParagraphNode();

        Text text1 = new Text("Big italic red text");
        text1.setFill(Color.RED);
        text1.setFont(Font.font("Helvetica", FontPosture.ITALIC, 40));

        Text text2 = new Text(" little bold blue text");
        text2.setFill(Color.BLUE);
        text2.setFont(Font.font("Helvetica", FontWeight.BOLD, 10));

        p1.getChildren().addAll(text1, text2);


        ParagraphNode p2 = new ParagraphNode();

        Text text3 = new Text("Lorem Ipsum Blafasel");
        text3.setFill(Color.RED);
        text3.setFont(Font.font("Helvetica", FontPosture.ITALIC, 40));

        Text text4 = new Text(" Lorem Ipsum");
        text4.setFill(Color.BLUE);
        text4.setFont(Font.font("Helvetica", FontWeight.BOLD, 10));

        URL imgUrl = AnimatedCounter.class.getResource("1.png");
        ImageView image1 = new ImageView(imgUrl.toString());

        Text text5 = new Text(" Blafasel");
        text5.setFill(Color.BLUE);
        text5.setFont(Font.font("Helvetica", FontWeight.BOLD, 10));

        p2.getChildren().addAll(text3, text4, image1, text5);

        ParagraphNode p3 = new ParagraphNode();
        Text text6 = new Text("public class Test {\n   public static void ");
        text6.setFill(Color.BLACK);
        text6.setFont(Font.font("Courier New", 12));
        Text text7 = new Text("main");
        text7.setFill(Color.RED);
        text7.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
        Text text8 = new Text("(String[] args) {\n      System.err.println(\"Hello World\");\n   }\n}");
        text8.setFill(Color.BLACK);
        text8.setFont(Font.font("Courier New", 12));

        p3.getChildren().addAll(text6, text7, text8);
*/
        // The main container for the whole rich text rendering.
        // In RichTextFX, this is a (vertical) VirtualFlow.
        paragraphs = new ParagraphLayout();
//        paragraphs.getChildren().addAll(selection, caret, p1, p3, p2);

        // The top level node which contains the paragraphs and 
        // additional artifacts like selection and caret
        // (in RichTextFX, this is part of the Paragraph node - not sure which approach is better)
        HBox mainLayout = new HBox();
        mainLayout.getChildren().addAll(selection, caret, paragraphs);

        refreshDocument();

        getChildren().add(mainLayout);
    }


    public void hit(double x, double y) {
        // find the paragraph
        ParagraphNode para = paragraphs.hit(x, y);
        if (para != null) {
            System.err.println("   CLICKED:" + para.getParagraph());
        }
    }


    public void refreshDocument() {
        paragraphs.getChildren().clear();

        Document<String, String> doc = control.getDocument();
        for (Paragraph<String, String> par : doc.getParagraphs()) {
            ParagraphNode paraNode = new ParagraphNode();
            String pstyle = par.getStyle();
            if (pstyle != null) {
                paraNode.getStyleClass().add(pstyle);
            }

            for (TextFragment<String> fragment : par.getFragments()) {
                Text textNode = new Text(fragment.getText());
                String style = fragment.getStyle();
                if (style != null) {
                    textNode.getStyleClass().add(style);
                }
                paraNode.getChildren().add(textNode);
            }

            paragraphs.getChildren().add(paraNode);
        }
    }
}
