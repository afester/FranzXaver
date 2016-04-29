package afester.javafx.examples.text.skin;

import java.net.URL;

import afester.javafx.components.Meter;
import afester.javafx.examples.animation.counter.AnimatedCounter;
import afester.javafx.examples.text.RichTextArea;
import afester.javafx.examples.text.model.Document;
import afester.javafx.examples.text.model.Paragraph;
import afester.javafx.examples.text.model.StyledText;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import static javafx.scene.AccessibleAttribute.OFFSET_AT_POINT;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;

/**
 * The top level node for the rich text editing area. 
 */
public class RichTextAreaView extends Region {

    // the main layout node. Contains all paragraphs layouted vertically.
    private ParagraphLayout paragraphs;

    private RichTextArea control;
    private Caret caret;
    private HBox mainLayout; 
    private boolean followCaretRequested = false;

    protected RichTextAreaView(RichTextArea control) {
        this.control = control;

        caret = new Caret();
        control.caretPositionProperty().addListener(e -> requestFollowCaret());

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

        // The main container for the whole rich text rendering.
        // In RichTextFX, this is a (vertical) VirtualFlow.
        paragraphs = new ParagraphLayout();
//        paragraphs.getChildren().addAll(selection, caret, p1, p3, p2);

        // The top level node which contains the paragraphs and 
        // additional artifacts like selection and caret
        // (in RichTextFX, this is part of the Paragraph node - not sure which approach is better)
        mainLayout = new HBox();
        mainLayout.getChildren().addAll(selection, caret, paragraphs);

        refreshDocument();

        getChildren().add(mainLayout);
    }


    public CharacterHit hit(double x, double y) {
        // find the paragraph
        ParagraphHit paraHit = paragraphs.hit(x, y);
        if (paraHit != null) {
            //int idx = paraHit.getFragmentIndex();
            //System.err.println("   CLICKED:" + paraHit.getNode() + "(" + idx + ")");

            ParagraphNode paragraph = paraHit.getNode();
            Point2D offset = paraHit.getCellOffset();
            System.err.println("OFFSET:" + offset);
            CharacterHit result = paragraph.hit(offset);


            int parIdx = paraHit.getParagraphIndex();
            System.err.println("  parIdx: " + parIdx);

            // Update caret - TODO: Move to different location
//            PathElement[] caretShape = paraHit.getNode().getCaretShape(result.getInsertionIndex(), false);
//            this.caret.setShape(caretShape);

            int parOffset = getParagraphOffset(parIdx);
            System.err.println("  parOffset: " + parOffset);
            result = result.offset(parOffset);

            System.err.println("  CharacterHit: " + result);
            return result;
        }

        return null;
    }


    private int getParagraphOffset(int parIdx) {
        return control.position(parIdx, 0).toOffset();
    }


    /**
     * event consumer / listener to setup the caret.
     * Called whenever the caret position changes.
     */
    private void requestFollowCaret() {
        followCaretRequested = true;
        //mainLayout.requestLayout();
        paragraphs.requestLayout();
    }


    /**
     * Assumption: this method makes sure that the rich text area follows the caret -
     * means, that the paragraph where the caret is positioned is always visible!
     * It is NOT about *positioning* the caret!
     */
    private void followCaret() {
        int parIdx = control.getCurrentParagraph();
        ParagraphNode para = (ParagraphNode) paragraphs.getChildren().get(parIdx);
        System.err.printf("Follow caret: %s - %s\n", parIdx, para);

//        Bounds caretBounds = cell.getNode().getCaretBounds();
//        double graphicWidth = cell.getNode().getGraphicPrefWidth();
//        Bounds region = extendLeft(caretBounds, graphicWidth);
//        virtualFlow.show(parIdx, region);
    }


    @Override
    protected void layoutChildren() {
      //  super.layoutChildren();
        mainLayout.resize(getWidth(), getHeight());
        
        // avoid unnecessary calls to followCaret - only when caret position changes!
        if(followCaretRequested) {
            followCaretRequested = false;
            followCaret();
        }

        System.err.println("RichTextAreaView.layoutChildren");
    }
    

    public void refreshDocument() {
        paragraphs.getChildren().clear();

        Document<String, String> doc = control.getDocument();
        for (Paragraph<String, String> par : doc.getParagraphs()) {
            ParagraphNode<String, String> paraNode = new ParagraphNode<>(par);
            String pstyle = par.getStyle();
            if (pstyle != null) {
                paraNode.getStyleClass().add(pstyle);
            }

            for (StyledText<String> fragment : par.getFragments()) {
                // RichTextFX uses some internal information from TextFlow through reflection 
                // to get the clicked character location. We try to use some more "official" 
                // approach - see 
                // http://stackoverflow.com/questions/32734645/how-do-i-get-the-character-index-at-a-given-coordinate-in-a-text-node
                Text textNode = new Text(fragment.getText());
                textNode.setOnMouseClicked(e -> {
                    Text t = (Text) e.getSource();
                    
                    final int idx = (int) t.queryAccessibleAttribute(OFFSET_AT_POINT, 
                            new Point2D(e.getScreenX(), e.getScreenY()));
                    System.err.println("CLICKED: " + t.getText() + "/" + idx);
                });

                String style = fragment.getStyle();
                if (style != null) {
                    textNode.getStyleClass().add(style);
                }
                paraNode.getChildren().add(textNode);
            }

            paragraphs.getChildren().add(paraNode);

            // TODO - hack! Initialize the index of each paragraph node.
            int idx = paragraphs.getChildren().indexOf(paraNode);
            paraNode.setIndex(idx);

            // caret is visible only in the paragraph with the caret
            control.currentParagraphProperty().addListener(
                    (obs, oldValue, newValue)
                 -> paraNode.caretVisibleProperty().setValue(idx == newValue.intValue()) );

            // update caret position whenever the control's caret column property changes
            // (TODO: only for current paragraph!! Otherwise paragraphs may contain invalid
            // caret positions)
            paraNode.caretPositionProperty().bind(control.caretColumnProperty());
                        //hasCaret.flatMap(has -> has
                        //? area.caretColumnProperty()

            // keep paragraph selection updated
            // The selection property of the paragraph is updated from the
            // control's selectionProperty!
            // ...
        }
    }
}
