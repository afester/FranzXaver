package afester.javafx.examples.controls;

import javafx.event.EventTarget;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
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


    public DrawingArea() {
        desk.setManaged(false);
        desk.resize(3000, 3000);
        desk.setStyle("-fx-border-style: solid; -fx-border-color: red;");

        transformationArea.setManaged(false);
        transformationArea.setTranslateX(1500);
        transformationArea.setTranslateY(1500);

        paper.setManaged(false);

        transformationArea.getChildren().add(paper);

        double deskCenterX = desk.getWidth()/2;
        double deskCenterY = desk.getHeight()/2;
        Line l1 = new Line(deskCenterX - 5, deskCenterY, deskCenterX + 5, deskCenterY);
        l1.setStroke(Color.RED);
        Line l2 = new Line(deskCenterX, deskCenterY - 5, deskCenterX, deskCenterY + 5);
        l2.setStroke(Color.RED);

        desk.getChildren().addAll(transformationArea, l1, l2);

        setContent(new Group(desk));
        setHbarPolicy(ScrollBarPolicy.ALWAYS);
        setVbarPolicy(ScrollBarPolicy.ALWAYS);
        setPannable(true);

        addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            if (event.getTarget() instanceof StackPane || (event.isControlDown() && event.isShiftDown())) {
            } else {
                EventTarget obj = event.getTarget();
                System.err.println(obj);
                if (obj instanceof RectangleObject) {
                    RectangleObject r = (RectangleObject) obj;
                    System.err.println("DRAG RECT" + r);

                    final Point2D mScene = new Point2D(event.getSceneX(), event.getSceneY());
                    Point2D dest = transformationArea.sceneToLocal(mScene);
                        
                    r.relocate(dest.getX(), dest.getY());
                }
                event.consume();
            }
        });

        addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.isControlDown()) {
                zoomView(event);
                event.consume();
            }
        });
        

        Line lp1 = new Line();
        lp1.setStroke(Color.GREEN);
        Line lp2 = new Line();
        lp2.setStroke(Color.GREEN);

        Bounds coordinatesBounds = transformationArea.getBoundsInParent();
        Rectangle cr = new Rectangle();
        cr.setFill(null);
        cr.setStroke(Color.GREEN);

        transformationArea.boundsInParentProperty().addListener((obj, oldValue, newValue) -> {
            System.err.println(newValue);
            double drawingAreaCenterX = transformationArea.getBoundsInParent().getWidth() / 2 + transformationArea.getBoundsInParent().getMinX();
            double drawingAreaCenterY = transformationArea.getBoundsInParent().getHeight() / 2  + transformationArea.getBoundsInParent().getMinY();
            lp1.setStartX(drawingAreaCenterX - 5);
            lp1.setStartY(drawingAreaCenterY);
            lp1.setEndX(drawingAreaCenterX + 5);
            lp1.setEndY(drawingAreaCenterY);

            lp2.setStartX(drawingAreaCenterX);
            lp2.setStartY(drawingAreaCenterY - 5);
            lp2.setEndX(drawingAreaCenterX);
            lp2.setEndY(drawingAreaCenterY + 5);

            cr.relocate(newValue.getMinX(), newValue.getMinY());
            cr.setWidth(newValue.getWidth());
            cr.setHeight(newValue.getHeight());
        });

        double drawingAreaCenterX = coordinatesBounds.getWidth() / 2 + coordinatesBounds.getMinX();
        double drawingAreaCenterY = coordinatesBounds.getHeight() / 2  + coordinatesBounds.getMinY();

        lp1.setStartX(drawingAreaCenterX - 5);
        lp1.setStartY(drawingAreaCenterY);
        lp1.setEndX(drawingAreaCenterX + 5);
        lp1.setEndY(drawingAreaCenterY);

        lp2.setStartX(drawingAreaCenterX);
        lp2.setStartY(drawingAreaCenterY - 5);
        lp2.setEndX(drawingAreaCenterX);
        lp2.setEndY(drawingAreaCenterY + 5);

        cr.relocate(coordinatesBounds.getMinX(), coordinatesBounds.getMinY());
        cr.setWidth(coordinatesBounds.getWidth());
        cr.setHeight(coordinatesBounds.getHeight());

        desk.getChildren().addAll(cr, lp1, lp2);

        Bounds paperBounds = paper.getBoundsInParent();
        paperBounds = transformationArea.localToParent(paperBounds);
        Rectangle pr = new Rectangle();
        pr.setFill(null);
        pr.setStroke(Color.MAGENTA);
        paper.boundsInParentProperty().addListener((obj, oldValue, newValue) -> {
            System.err.println(newValue);
            newValue = transformationArea.localToParent(newValue);
            pr.relocate(newValue.getMinX(), newValue.getMinY());
            pr.setWidth(newValue.getWidth());
            pr.setHeight(newValue.getHeight());
        });
        pr.relocate(paperBounds.getMinX(), paperBounds.getMinY());
        pr.setWidth(paperBounds.getWidth());
        pr.setHeight(paperBounds.getHeight());
        desk.getChildren().add(pr);

        setVvalue(0.5);
        setHvalue(0.5);
     //   centerContent();
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

        // store current mouse coordinates as a reference point
        // final Point2D mPos = new Point2D(e.getSceneX(), e.getSceneY());    // scene coordinates of mouse
        final Point2D mPosContent = desk.sceneToLocal(centerPoint);               // position in content coordinates

        final double oldHvalue = getHvalue();
        final double oldVvalue = getVvalue();
        System.err.printf("Scrollbar: %s/%s\n", oldHvalue, oldVvalue);

        // scale the content
        transformationArea.setScaleX(scaleFactor);
        transformationArea.setScaleY(scaleFactor);
        //paper.setScaleX(scaleFactor);
        //paper.setScaleY(scaleFactor);

        final Point2D mPosContent2 = desk.sceneToLocal(centerPoint);               // position in content coordinates
        System.err.println("OLD: " + mPosContent);
        System.err.println("NEW: "+ mPosContent2);
        System.err.printf("Scrollbar: %s/%s\n", getHvalue(), getVvalue());

        Point2D delta = mPosContent2.subtract(mPosContent);
        System.err.println("DELTA: "+ delta);

        setHvalue(oldHvalue);
        setVvalue(oldVvalue);

//        // move the content so that the reference point is again at the mouse position
//        Point2D dviewPos = desk.localToParent(mPosContent);  // reference point in dview coordinates
//        Point2D dviewMouse = sceneToLocal(mPos);                // destination point
//        Point2D diff = dviewMouse.subtract(dviewPos);
//        
//        content.setLayoutX(content.getLayoutX() + diff.getX());
//        content.setLayoutY(content.getLayoutY() + diff.getY());
//
//        scrollPane.setHvalue(0.5);
//        scrollPane.setVvalue(0.5);
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

        final double margin = 10;
        double wFactor = (viewBounds.getWidth() - margin) / paperBounds.getWidth();
        double hFactor = (viewBounds.getHeight() - margin) / paperBounds.getHeight();
        scaleFactor = Math.min(wFactor,  hFactor);

        setScale(scaleFactor, new Point2D(0, 0));
        centerContent();
    }
}
