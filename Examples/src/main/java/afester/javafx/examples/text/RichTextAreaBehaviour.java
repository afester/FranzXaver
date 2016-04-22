package afester.javafx.examples.text;


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
        // area.setOnKeyTyped(e -> System.err.println("Key typed."));
    }


    private void mousePressed(MouseEvent e) {
        // e.getTarget(); // the target Node ("Text")

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

}
