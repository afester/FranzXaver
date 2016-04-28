package afester.javafx.examples.text.skin;


import afester.javafx.examples.text.RichTextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;


public class RichTextAreaBehaviour {

    private RichTextArea control;
    private RichTextAreaView view;


    public RichTextAreaBehaviour(RichTextArea control, RichTextAreaSkin skin) {
        this.control = control;
        this.view = skin.getTopLevelNode();

        System.err.println("SKIN:" + this.view);

        control.requestFocus();    // required to get keyboard events

        control.setOnMouseClicked(this::mousePressed);
        control.setOnKeyPressed(this::keyPressed);
        control.setOnKeyTyped(this::keyTyped);
    }


    private void mousePressed(MouseEvent e) {
        // e.getTarget(); // the target Node ("Text")

        control.requestFocus();

        CharacterHit hit = view.hit(e.getX(), e.getY());
        control.moveTo(hit.getInsertionIndex()); // , SelectionPolicy.CLEAR);

        //System.err.println(hit.getIn);
/*
        e.getX();
        e.getY();
        e.getClickCount();

        System.err.printf("Mouse clicked (%f / %f)\n", e.getX(), e.getY());
*/
    }


    private void keyPressed(KeyEvent e) {
        System.err.println("Key pressed:" + e);
    }


    private void keyTyped(KeyEvent event) {
        System.err.printf("Typed: %s\n", event);
        
        String text = event.getCharacter();
        int n = text.length();

        if(n == 0) {
            return;
        }

        System.err.printf("Typed: %s\n", text);
        control.replaceSelection(text);
    }

}
