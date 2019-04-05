package afester.javafx.examples.board;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

// TODO: This also needs to support vertical and horizontal scrolling
public class DrawingView extends Pane {
    private double mx = 0;
    private double my = 0;
    private double scaleFactor = 3.7;
    private Pane content;

    public DrawingView(Pane pContent) {
        this.content = pContent;

        content.setScaleX(scaleFactor);
        content.setScaleY(scaleFactor);

        Rectangle clipRectangle = new Rectangle();
        setClip(clipRectangle);
        layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            clipRectangle.setWidth(newValue.getWidth());
            clipRectangle.setHeight(newValue.getHeight());
        });

        // the Pane also has the mouse listeners for pan and zoom in order to be able to catch them at any position 
        setOnMousePressed( e-> {
            if (e.isControlDown()) {
                // System.err.println("VIEW:" + e);

                mx = e.getX();
                my = e.getY();
            }
//            else {
//                EventTarget target = e.getTarget();
//                if (target instanceof SelectionShape) {
//                    setSelected((SelectionShape) target);
//                    mx = e.getX();
//                    my = e.getY();
//                    
//                } else {
//                    setSelected(null);
//                }
//            }
        });

        setOnMouseDragged(e -> {
            if (e.isControlDown()) {
                // System.err.println("VIEW:" + e);

                double dx = mx - e.getX();
                double dy = my - e.getY();
                mx = e.getX();
                my = e.getY();
                content.setLayoutX(content.getLayoutX() - dx);
                content.setLayoutY(content.getLayoutY() - dy);
            } 
//            else {
//                if (currentSelection != null) {
//                    double dx = mx - e.getX();
//                    double dy = my - e.getY();
//                    mx = e.getX();
//                    my = e.getY();
//                    currentSelection.setLayoutX(currentSelection.getLayoutX() - dx);
//                    currentSelection.setLayoutY(currentSelection.getLayoutY() - dy);
//                }
//            }
        });

        setOnScroll(e-> {
            if (e.isControlDown()) {
                System.err.println(e);
                double delta = 0.5;
                if (e.getDeltaY() < 0) {
                    delta = - 0.5;
                }
                double newScale = scaleFactor + delta;
                if (newScale > 0) {

                    Point2D before = content.localToParent(e.getX(), e.getY());
                    System.err.printf("BEFORE: %s\n", before);

                    scaleFactor = newScale;
                    updateZoom();

//                    Point2D after = content.localToParent(e.getX(), e.getY());
//                    System.err.printf("AFTER: %s\n", after);
//
//                    Point2D diff = after.subtract(before);
//                    System.err.printf("DIFF: %s\n", diff);
//
//                    content.setLayoutX(content.getLayoutX() - diff.getX()*scaleFactor);
//                    content.setLayoutY(content.getLayoutY() - diff.getY()*scaleFactor);
//
//                    Point2D after2 = content.localToParent(e.getX(), e.getY());
//                    System.err.printf("AFTER2: %s\n", after2);
                }
            }
        });

        getChildren().add(content);
    }


    private void updateZoom() {
        System.err.printf("Factor: %s\n", scaleFactor);
        content.setScaleX(scaleFactor);
        content.setScaleY(scaleFactor);
    }


    /**
     * Centers the content pane so that it is in the center of the DrawingArea.
     */
    public void centerContent() {
        Bounds daBounds = getLayoutBounds();
        Bounds contentBounds = content.getLayoutBounds();

        System.err.println("DrawingArea:" + daBounds);
        System.err.println("Contents   :" + contentBounds);

        double xpos = (daBounds.getWidth() - contentBounds.getWidth()) / 2;
        double ypos = (daBounds.getHeight() - contentBounds.getHeight()) / 2;
        
        content.setLayoutX(xpos);
        content.setLayoutY(ypos);
    }


    /**
     * Scales and centers the content pane so that it fits into the DrawingArea.
     */
    public void fitContentToWindow() {
        Bounds daBounds = getLayoutBounds();
        Bounds contentBounds = content.getLayoutBounds();

        System.err.println("DrawingArea:" + daBounds);
        System.err.println("Contents   :" + contentBounds);

        final double margin = 10;
        double wFactor = (daBounds.getWidth() - margin) / contentBounds.getWidth();
        double hFactor = (daBounds.getHeight() - margin) / contentBounds.getHeight();
        scaleFactor = Math.min(wFactor,  hFactor);

        updateZoom();
        centerContent();
    }
}
