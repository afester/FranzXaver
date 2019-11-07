package afester.javafx.components;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * ScrollPane
 *    +-- viewPort: 
 *    `-- content: Group
 *          +-- desk: Pane (maximum area)
 *                +-- transformationArea: Pane
 *                      +-- paper: Group
 *                            +-- Obj1
 *                            +-- Obj2
 *                            +-- Obj3
 *                            ...
 */
public class DrawingArea extends ScrollPane {

    private final Pane desk = new Pane();
    private final Pane transformationArea = new Pane();
    private final Group paper = new Group();
    private final Insets paperBorder = new Insets(10, 10, 10, 10);
    private final Insets fitPaperMargin = new Insets(5, 5, 5, 5);

    private boolean panning = false;

    public DrawingArea() {
        desk.setManaged(false);
        desk.resize(3000, 3000);
        // desk.setStyle("-fx-border-style: solid; -fx-border-color: red;");

        transformationArea.setManaged(false);
        transformationArea.setTranslateX(1500);
        transformationArea.setTranslateY(1500);

        paper.setManaged(false);

        transformationArea.getChildren().add(paper);

//        double deskCenterX = desk.getWidth()/2;
//        double deskCenterY = desk.getHeight()/2;
//        Line l1 = new Line(deskCenterX - 5, deskCenterY, deskCenterX + 5, deskCenterY);
//        l1.setStroke(Color.RED);
//        Line l2 = new Line(deskCenterX, deskCenterY - 5, deskCenterX, deskCenterY + 5);
//        l2.setStroke(Color.RED);

        pr.setFill(Color.WHITE);
        pr.setStroke(Color.GRAY);

        Bounds paperBounds = paper.getBoundsInParent();
        updatePaperBoundsRect(paperBounds);
        paper.boundsInParentProperty().addListener((obj, oldValue, newValue) -> {
            updatePaperBoundsRect(newValue);
        });

        desk.getChildren().addAll(pr, transformationArea); // , l1, l2);

        setContent(new Group(desk));
        setHbarPolicy(ScrollBarPolicy.ALWAYS);
        setVbarPolicy(ScrollBarPolicy.ALWAYS);

        setOnMousePressed(e -> {
           if (e.isControlDown() && e.isShiftDown()) {
               panning = true;
               setPannable(true);
           }
        });

        setOnMouseReleased(e -> {
            if (panning) {
                panning = false;
                setPannable(false);
            }
         });


//        addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
//            if (!isScrollPaneEvent(event)) {
//                if (interactor != null) {
//                    interactor.mousePressed(event);
//                }
//                event.consume();
//            }
//        });
//
//        addEventFilter(MouseEvent.DRAG_DETECTED, event -> {
//            if (!isScrollPaneEvent(event)) {
//                event.consume();
//            }            
//        });
//
//        addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
//            if (!isScrollPaneEvent(event)) {
//                if (interactor != null) {
//                    interactor.mouseDragged(event);
//                }
//                event.consume();
//            }
//        });
//
//        addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
//            if (!isScrollPaneEvent(event)) {
//                if (interactor != null) {
//                    interactor.mouseReleased(event);
//                }
//                event.consume();
//            }            
//        });

        addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.isControlDown()) {
                zoomView(event);
                event.consume();
            }
        });

//        Line lp1 = new Line();
//        lp1.setStroke(Color.GREEN);
//        Line lp2 = new Line();
//        lp2.setStroke(Color.GREEN);
//
//        Bounds coordinatesBounds = transformationArea.getBoundsInParent();
//        Rectangle cr = new Rectangle();
//        cr.setFill(null);
//        cr.setStroke(Color.GREEN);
//
//        transformationArea.boundsInParentProperty().addListener((obj, oldValue, newValue) -> {
//            System.err.println(newValue);
//            double drawingAreaCenterX = transformationArea.getBoundsInParent().getWidth() / 2 + transformationArea.getBoundsInParent().getMinX();
//            double drawingAreaCenterY = transformationArea.getBoundsInParent().getHeight() / 2  + transformationArea.getBoundsInParent().getMinY();
//            lp1.setStartX(drawingAreaCenterX - 5);
//            lp1.setStartY(drawingAreaCenterY);
//            lp1.setEndX(drawingAreaCenterX + 5);
//            lp1.setEndY(drawingAreaCenterY);
//
//            lp2.setStartX(drawingAreaCenterX);
//            lp2.setStartY(drawingAreaCenterY - 5);
//            lp2.setEndX(drawingAreaCenterX);
//            lp2.setEndY(drawingAreaCenterY + 5);
//
//            cr.relocate(newValue.getMinX(), newValue.getMinY());
//            cr.setWidth(newValue.getWidth());
//            cr.setHeight(newValue.getHeight());
//        });
//
//        double drawingAreaCenterX = coordinatesBounds.getWidth() / 2 + coordinatesBounds.getMinX();
//        double drawingAreaCenterY = coordinatesBounds.getHeight() / 2  + coordinatesBounds.getMinY();
//
//        lp1.setStartX(drawingAreaCenterX - 5);
//        lp1.setStartY(drawingAreaCenterY);
//        lp1.setEndX(drawingAreaCenterX + 5);
//        lp1.setEndY(drawingAreaCenterY);
//
//        lp2.setStartX(drawingAreaCenterX);
//        lp2.setStartY(drawingAreaCenterY - 5);
//        lp2.setEndX(drawingAreaCenterX);
//        lp2.setEndY(drawingAreaCenterY + 5);
//
//        cr.relocate(coordinatesBounds.getMinX(), coordinatesBounds.getMinY());
//        cr.setWidth(coordinatesBounds.getWidth());
//        cr.setHeight(coordinatesBounds.getHeight());
//
//        desk.getChildren().addAll(cr, lp1, lp2);

//////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    private final  Rectangle pr = new Rectangle();
    private void updatePaperBoundsRect(Bounds b) {
        Bounds paperBounds = transformationArea.localToParent(b);
        pr.relocate(paperBounds.getMinX() - paperBorder.getLeft(), paperBounds.getMinY() - paperBorder.getTop());
        pr.setWidth(paperBounds.getWidth() + paperBorder.getLeft() + paperBorder.getRight());
        pr.setHeight(paperBounds.getHeight() + paperBorder.getTop() + paperBorder.getBottom());
    }

    private double scaleFactor = 1.0; // 3.7;
    private static final double SCALE_STEP = Math.sqrt(1.3);

    private void zoomView(ScrollEvent e) {
        // calculate the new scale factor
        double newScale;
        if (e.getDeltaY() < 0) {
            newScale = scaleFactor / SCALE_STEP;
        }else {
            newScale = scaleFactor * SCALE_STEP;
        }
        if (newScale > 0) {
            System.err.println("SCALE: " + newScale);
            setScale(newScale, new Point2D(e.getSceneX(), e.getSceneY()));
        }
    }

    public void setScale(double newScale, Point2D centerPoint) {
        scaleFactor = newScale;

        // scale the content

        Point2D paperPos = paper.sceneToLocal(centerPoint);
        Point2D deskPos = paper.localToParent(paperPos);
        Point2D oldPos = transformationArea.localToParent(deskPos);
        System.err.println("PAPER POS:" + oldPos);

        transformationArea.setScaleX(scaleFactor);
        transformationArea.setScaleY(scaleFactor);

        Point2D deskPos2 = paper.localToParent(paperPos);
        System.err.println("PAPER POS1:" + transformationArea.localToParent(deskPos2));

        Point2D delta = transformationArea.localToParent(deskPos2).subtract(oldPos);
        System.err.println("DELTA:" + delta);

        Bounds viewBounds = getViewportBounds();

        double maxHdist = desk.getWidth() - viewBounds.getWidth();
        double maxVdist = desk.getHeight() - viewBounds.getHeight();
        System.err.println("maxHdist:" + maxHdist);
        System.err.println("maxVdist:" + maxVdist);

        double hValue = delta.getX() / maxHdist;
        double vValue = delta.getY() / maxVdist;

        System.err.println("delta hValue:" + hValue);
        System.err.println("delta vValue:" + vValue);

        setVvalue(getVvalue() + vValue);
        setHvalue(getHvalue() + hValue);

        updatePaperBoundsRect(paper.getBoundsInParent());
    }

    public void centerContent() {
        Bounds paperBounds = paper.getBoundsInParent();
        Bounds paperBounds2 = transformationArea.localToParent(paperBounds);  // bounds on desk
        Bounds viewBounds = getViewportBounds();

        Point2D paperCenter = new Point2D(paperBounds2.getMinX() + paperBounds2.getWidth()/2,
                                          paperBounds2.getMinY() + paperBounds2.getHeight()/2);

        System.err.println("Paper      :" + paperBounds2);
        System.err.println("PaperCenter:" + paperCenter);
        System.err.println("ViewPort   :" + viewBounds);

        Point2D viewPortOrg = paperCenter.subtract(new Point2D(viewBounds.getWidth()/2, viewBounds.getHeight()/2));
        System.err.println("ViewPortOrg:" + viewPortOrg);

        double maxHdist = desk.getWidth() - viewBounds.getWidth();
        double maxVdist = desk.getHeight() - viewBounds.getHeight();
        System.err.println("maxHdist:" + maxHdist);
        System.err.println("maxVdist:" + maxVdist);

        double hValue = viewPortOrg.getX() / maxHdist;
        double vValue = viewPortOrg.getY() / maxVdist;

        System.err.println("hValue:" + hValue);
        System.err.println("vValue:" + vValue);

        setHvalue(hValue);
        setVvalue(vValue);
    }

    public Group getPaper() {
        return paper;
    }

    public void fitContentToWindow() {
        Bounds paperBounds = paper.getBoundsInParent();
        Bounds viewBounds = getViewportBounds();

        System.err.println("Paper:" + paperBounds);
        System.err.println("View :" + viewBounds);

        
        final double overallMarginW = paperBorder.getLeft() + paperBorder.getRight() + fitPaperMargin.getLeft() + fitPaperMargin.getRight();
        final double overallMarginH = paperBorder.getTop() + paperBorder.getBottom() + fitPaperMargin.getTop() + fitPaperMargin.getBottom();

        double wFactor = (viewBounds.getWidth() - overallMarginW) / paperBounds.getWidth();
        double hFactor = (viewBounds.getHeight() - overallMarginH) / paperBounds.getHeight();
        scaleFactor = Math.min(wFactor,  hFactor);

        setScale(scaleFactor, new Point2D(0, 0));
        centerContent();
    }
}
