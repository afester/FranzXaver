package afester.javafx.examples.text.skin;

import afester.javafx.examples.text.RichTextArea;
import javafx.scene.control.SkinBase;

/**
 * The skin for the Rich Text Area.
 */
public class RichTextAreaSkin extends SkinBase<RichTextArea> {

    private final RichTextAreaView node;

    public RichTextAreaSkin(RichTextArea control) {
//                                BiConsumer<? super TextExt, S> applyStyle,
//                                PS initialParagraphStyle,
//                                BiConsumer<TextFlow, PS> applyParagraphStyle) {
        super(control);
        this.node = new RichTextAreaView(control); // , applyStyle, initialParagraphStyle, applyParagraphStyle);
        getChildren().add(this.node);
    }
/*
    @Override
    public void dispose() {
        node.dispose();
    }

    @Override
*/    public RichTextAreaView getTopLevelNode() {
        return node;
    }
/*
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return node.getCssMetaData();
    }
*/
}
