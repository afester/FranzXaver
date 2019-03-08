package afester.javafx.examples.board;

import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

class Net extends Group {

    public Net() {
        Line l1 = new Line(1*2.54, 1*2.54, 20*2.54,  1*2.54);
        l1.setStroke(Color.RED);
        l1.setStrokeWidth(0.8);

        Line l2 = new Line(3*2.54, 1*2.54,  3*2.54, 10*2.54);
        l2.setStroke(Color.RED);
        l2.setStrokeWidth(0.8);

        getChildren().addAll(l1, l2);
    }
}


public class Board extends Group {

    private Color boardColor = Color.rgb(0xcc,  0x82,  0x11, 1.0);
    private Color traceColor = Color.rgb(0xf8, 0xe7, 0xbe, 1.0);
    private double scaleFactor = 3.7;
    private Group partsGroup;
    private Group wireGroup;

    public Board(float length, float width) {

        Rectangle board = new Rectangle(length, width, boardColor);
        board.setStroke(Color.BLACK);

        this.getChildren().add(board);

        for (double ypos = 2.54;  ypos < 100;  ypos += 2.54 ) {
            for (double xpos = 2.54;  xpos < 160;  xpos += 2.54) {
                Circle c = new Circle(0.7);
                c.setFill(Color.WHITE);
                c.setStroke(traceColor);
                c.setStrokeWidth(0.6);
                c.setCenterX(xpos);
                c.setCenterY(ypos);

                this.getChildren().add(c);
            }
        }
        partsGroup = new Group();
        getChildren().add(partsGroup);
        wireGroup = new Group();
        getChildren().add(wireGroup);

        setScaleX(scaleFactor);
        setScaleY(scaleFactor);

        setOnMousePressed(e -> mousePressed(e));
        setOnMouseDragged(e -> mouseDragged(e));
        setOnScroll(e-> rotate(e));
    }

    private double mx = 0;
    private double my = 0;
    

    private void mousePressed(MouseEvent e) {
        System.err.println(e);
        
        if (e.isControlDown()) {
            mx = e.getX();
            my = e.getY();
        }else {
            Net net = new Net();
            wireGroup.getChildren().add(net);
        }

    }

    private void mouseDragged(MouseEvent e) {
        if (e.isControlDown()) {
            double dx = mx - e.getX();
            double dy = my - e.getY();
    
            setLayoutX(getLayoutX() - dx);
            setLayoutY(getLayoutY() - dy);
        }
    }

    private void rotate(ScrollEvent e) {
        if (e.isControlDown()) {
            System.err.println(e);
            if (e.getDeltaY() > 0) {
                scaleFactor = scaleFactor + 0.5;
            } else {
                scaleFactor = scaleFactor - 0.5;
            }

            setScaleX(scaleFactor);
            setScaleY(scaleFactor);
        }
    }
}
