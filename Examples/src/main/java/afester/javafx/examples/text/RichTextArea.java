package afester.javafx.examples.text;

import javafx.scene.control.Control;

public class RichTextArea extends Control {

    private RichTextAreaSkin skin;
    private Document document;

    public RichTextArea() {
    }


    @Override
    protected javafx.scene.control.Skin<?> createDefaultSkin() {
        // See http://tomasmikula.github.io/blog/2014/06/11/separation-of-view-and-controller-in-javafx-controls.html
        RichTextAreaSkin skin = new RichTextAreaSkin(this);
        RichTextAreaBehaviour behaviour = new RichTextAreaBehaviour(this, skin);
        return skin;
    }


    public void setDocument(Document doc) {
        this.document = doc;
        if (skin != null) {
            skin.refreshDocument();
        }
    }


    public Document getDocument() {
        return document;
    }
}
