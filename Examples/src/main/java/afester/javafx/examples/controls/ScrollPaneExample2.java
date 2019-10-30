package afester.javafx.examples.controls;

import afester.javafx.examples.Example;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;


@Example(desc = "ScrollPane",
         cat  = "JavaFX")
public class ScrollPaneExample2 extends Application {
    
    private Pane paper;
    private Pane desk; 
    private ScrollPane scrollPane;

    
    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX scrollpane example");

        desk = new Pane();
        desk.setManaged(false);
        desk.resize(3000, 3000);
        desk.setStyle("-fx-border-style: solid; -fx-border-color: red;");

        paper = new Pane();
        paper.setManaged(false);
        paper.setStyle("-fx-border-style: solid; -fx-border-color: black;");

        RectangleObject r1 = new RectangleObject("R1", Color.RED, new Point2D(-50, -50), 100, 30);
        RectangleObject r2 = new RectangleObject("R2", Color.BLUE, new Point2D(0, 0), 40, 40);
        RectangleObject r3 = new RectangleObject("R3", Color.GREEN, new Point2D(-50, 100), 50, 50);
        RectangleObject r4 = new RectangleObject("R4", Color.ORANGE, new Point2D(100, 100), 50, 50);
        paper.getChildren().addAll(r1, r2, r3, r4);

        double deskCenterX = desk.getWidth()/2;
        double deskCenterY = desk.getHeight()/2;
        Line l1 = new Line(deskCenterX - 5, deskCenterY, deskCenterX + 5, deskCenterY);
        l1.setStroke(Color.RED);
        Line l2 = new Line(deskCenterX, deskCenterY - 5, deskCenterX, deskCenterY + 5);
        l2.setStroke(Color.RED);
        desk.getChildren().addAll(paper, l1, l2);

        scrollPane = new ScrollPane(new Group(desk));
        scrollPane.setHbarPolicy(ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        scrollPane.setPannable(true);

        scrollPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            if (event.isControlDown() && event.isShiftDown()) {
            } else {
                System.err.println("DRAG OBJ");
                event.consume();
            }
        });

        scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.isControlDown()) {
                zoomView(event);
                event.consume();
            }
        });

        Scene scene = new Scene(scrollPane, 1000, 800);
        primaryStage.setScene(scene);
        primaryStage.show();

        Bounds paperBounds = paper.getBoundsInLocal();
        Rectangle pr = new Rectangle(paperBounds.getMinX(), paperBounds.getMinY(), paperBounds.getWidth(), paperBounds.getHeight());
        pr.setFill(null);
        pr.setStroke(Color.MAGENTA);

        double paperCenterX = paperBounds.getWidth()/2 + paperBounds.getMinX();
        double paperCenterY = paperBounds.getHeight()/2 + paperBounds.getMinY();
        Line lp1 = new Line(paperCenterX - 5, paperCenterY, paperCenterX + 5, paperCenterY);
        lp1.setStroke(Color.MAGENTA);
        Line lp2 = new Line(paperCenterX, paperCenterY - 5, paperCenterX, paperCenterY + 5);
        lp2.setStroke(Color.MAGENTA);
        
        paper.getChildren().addAll(lp1, lp2, pr);

        centerContent();
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
            scaleFactor = newScale;
            System.err.println("SCALE: " + scaleFactor);

            // store current mouse coordinates as a reference point
            final Point2D mPos = new Point2D(e.getSceneX(), e.getSceneY());    // scene coordinates of mouse
            final Point2D mPosContent = desk.sceneToLocal(mPos);               // position in content coordinates

            final double oldHvalue = scrollPane.getHvalue();
            final double oldVvalue = scrollPane.getVvalue();
            System.err.printf("Scrollbar: %s/%s\n", oldHvalue, oldVvalue);

            // scale the content
            desk.setScaleX(scaleFactor);
            desk.setScaleY(scaleFactor);

            final Point2D mPosContent2 = desk.sceneToLocal(mPos);               // position in content coordinates
            System.err.println("OLD: " + mPosContent);
            System.err.println("NEW: "+ mPosContent2);
            System.err.printf("Scrollbar: %s/%s\n", scrollPane.getHvalue(), scrollPane.getVvalue());

            Point2D delta = mPosContent2.subtract(mPosContent);
            System.err.println("DELTA: "+ delta);

            scrollPane.setHvalue(oldHvalue);
            scrollPane.setVvalue(oldVvalue);

//            // move the content so that the reference point is again at the mouse position
//            Point2D dviewPos = desk.localToParent(mPosContent);  // reference point in dview coordinates
//            Point2D dviewMouse = sceneToLocal(mPos);                // destination point
//            Point2D diff = dviewMouse.subtract(dviewPos);
//            
//            content.setLayoutX(content.getLayoutX() + diff.getX());
//            content.setLayoutY(content.getLayoutY() + diff.getY());
//
//            scrollPane.setHvalue(0.5);
//            scrollPane.setVvalue(0.5);
        }
    }

    private void centerContent() {
        Bounds paperBounds = paper.getBoundsInLocal();
        Point2D paperCenter = new Point2D(paperBounds.getWidth()/2 + paperBounds.getMinX(),
                                          paperBounds.getHeight()/2 + paperBounds.getMinY());
        Point2D deskCenter = new Point2D(desk.getWidth()/2, desk.getHeight()/2);
        Point2D paperCenterInParent = paper.localToParent(paperCenter);
        Point2D dist = deskCenter.subtract(paperCenterInParent);

        Point2D pos = new Point2D(paper.getLayoutX(), paper.getLayoutY());
        Point2D newPos = pos.add(dist);
        paper.relocate(newPos.getX(), newPos.getY());

        scrollPane.setHvalue(0.5);
        scrollPane.setVvalue(0.5);
    }
}
