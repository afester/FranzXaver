package afester.javafx.examples.transform;

import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

class MyCircle extends Group {
    public MyCircle(double mx, double my, double r) {
        Circle circle = new Circle(mx, my, r);
        circle.setStroke(Color.BLUE);
        circle.setFill(Color.YELLOW);

        Cross cross = new Cross(mx, my, 3);
        getChildren().addAll(circle, cross);
    }
}


class Cross extends Group {
    public Cross(double mx, double my, double len) {
        Line line1 = new Line(mx-len, my, mx+len, my);
        line1.setStroke(Color.RED);
        Line line2 = new Line(mx, my-len, mx, my+len);
        line2.setStroke(Color.RED);

        getChildren().addAll(line1, line2);
    }
}

class DrawingView extends Pane {
    private Pane content;

    public DrawingView(Pane pContent) {
        this.content = pContent;

        getChildren().add(pContent);


    }
}


public class Scaling extends Application {
    private double scaleFactor = 1.0;
    private Group boundVisuals;
    //private Group content;
    private Pane content;
    private Pane dView;
    private static final double SCALE_STEP = Math.sqrt(1.3);

    @Override
    public void start(Stage stage) {

        // create some content
        MyCircle circle = new MyCircle(100, 100, 30);
        MyCircle circle2 = new MyCircle(150, 120, 40);
        MyCircle circle3 = new MyCircle(200, 140, 50);

        // A Group is just a list of children - the group dimensions are calculated from the bounding box of all children
        // A Pane (seems to have) a fixed x/y location and the width/height extends to the rightmost/bottommost coordinates of it children 
        //content = new Group(circle, circle2, circle3);
        content = new Pane(circle, circle2, circle3);

        // add the content to a DrawingView for scaling and panning
        dView = new Pane(content);
        dView.setManaged(false);
        
        dView.setOnScroll(e -> {
            //System.err.println(e);

            double newScale;
            if (e.getDeltaY() < 0) {
                newScale = scaleFactor / SCALE_STEP;
            }else {
                newScale = scaleFactor * SCALE_STEP;
            }
            if (newScale > 0) {
                scaleFactor = newScale;

                final Point2D mPos = new Point2D(e.getSceneX(), e.getSceneY());       // scene coordinates of mouse
                final Point2D mPosContent = content.sceneToLocal(mPos);               // position in content coordinates

//                Circle cross = new Circle(mPosContent.getX(), mPosContent.getY(), 1.0);
//                cross.setFill(null);
//                cross.setStroke(Color.BLUE);
//                content.getChildren().add(cross);

                content.setScaleX(scaleFactor);
                content.setScaleY(scaleFactor);

                Point2D dviewPos = content.localToParent(mPosContent);  // reference point in dview coordinates
                Point2D dviewMouse = dView.sceneToLocal(mPos);          // destination point
                Point2D diff = dviewMouse.subtract(dviewPos);
                
                content.setLayoutX(content.getLayoutX() + diff.getX());
                content.setLayoutY(content.getLayoutY() + diff.getY());
            }
        });

        boundVisuals = new Group();
        // Group mainGroup = new Group(dView, boundVisuals);
        dView.getChildren().add(boundVisuals);
        BorderPane mainLayout = new BorderPane();
        mainLayout.setCenter(dView);

        HBox buttons = new HBox();
        Button b1 = new Button("Rotate");
        b1.setOnAction(e -> rotateContent());
        Button b2 = new Button("Show geometry");
        b2.setOnAction(e -> updateBoundVisuals());
        buttons.getChildren().addAll(b1, b2);
        mainLayout.setBottom(buttons);

        Scene mainScene = new Scene(mainLayout, 800, 600);
        stage.setScene(mainScene);
        stage.show();
    }

    private int currentRotate = 0;
    private Point2D pivot = new Point2D(200, 140);

    private void rotateContent() {
        currentRotate += 45;
        if (currentRotate > 360) {
            currentRotate = 0;
        }

        Rotate r = new Rotate(currentRotate, pivot.getX(), pivot.getY());
        content.getTransforms().clear();
        content.getTransforms().add(r);
        // content.setRotate(currentRotate);
        updateBoundVisuals();
    }

    private void updateBoundVisuals() {
        boundVisuals.getChildren().clear();

        Bounds cb = content.getBoundsInParent();
        //System.err.println(cb);
        //System.err.println(content.getBoundsInLocal());
        Rectangle cbr = new Rectangle(cb.getMinX(), cb.getMinY(), cb.getWidth(), cb.getHeight());
        cbr.setStroke(Color.BLUE);
        cbr.setFill(null);

//        Point2D pivot = new Point2D(cb.getMinX() + cb.getWidth()/2,
//                                     cb.getMinY() + cb.getHeight()/2);
//        Point2D pivot = new Point2D(200, 140);
        System.err.println(pivot);
        Circle c2 = new Circle(pivot.getX(), pivot.getY(), 1);
        c2.setFill(null);
        c2.setStroke(Color.BLUE);

        Line l1 = new Line(cb.getMinX(), cb.getMinY(), cb.getMinX()+cb.getWidth(), cb.getMinY()+cb.getHeight());
        Line l2 = new Line(cb.getMinX(), cb.getMinY()+cb.getHeight(), cb.getMinX()+cb.getWidth(), cb.getMinY());
        l1.setStrokeWidth(0.5);
        l2.setStrokeWidth(0.5);

//        String coords = String.format("%s/%s %sx%s",  cb.getMinX(), cb.getMinY(), cb.getWidth(), cb.getHeight());
//        Text t1 = new Text(cb.getMinX() + cb.getWidth(), cb.getMinY() + cb.getHeight() - 20, coords);
//        t1.setStroke(Color.BLUE);
//
//        Bounds dv = dView.getBoundsInParent();
//        Rectangle dvr = new Rectangle(dv.getMinX(), dv.getMinY(), dv.getWidth(), dv.getHeight());
//        dvr.setStroke(Color.RED);
//        dvr.setFill(null);
//        coords = String.format("%s/%s %sx%s",  dv.getMinX(), dv.getMinY(), dv.getWidth(), dv.getHeight());
//        Text t = new Text(dv.getMinX() + dv.getWidth(), dv.getMinY() + dv.getHeight(), coords);
//        t.setStroke(Color.RED);
//
//        Point2D m = boundVisuals.sceneToLocal(new Point2D(10, 10));
//        Line line1 = new Line(m.getX()-5, m.getY(), m.getX()+5, m.getY());
//        line1.setStroke(Color.RED);
//        Line line2 = new Line(m.getX(), m.getY()-5, m.getX(), m.getY()+5);
//        line2.setStroke(Color.RED);
//
//        boundVisuals.getChildren().addAll(cbr, dvr, t1, t, line1, line2);
        boundVisuals.getChildren().addAll(cbr, c2, l1, l2);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }
}
