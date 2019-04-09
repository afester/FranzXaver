package afester.javafx.examples.transform;

import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

class MyCircle extends Group {
    public MyCircle(double mx, double my, double r) {
        Circle circle = new Circle(mx, my, r);
        circle.setStroke(Color.BLUE);
        circle.setFill(Color.YELLOW);
        Line line1 = new Line(mx-5, my, mx+5, my);
        line1.setStroke(Color.RED);
        Line line2 = new Line(mx, my-5, mx, my+5);
        line2.setStroke(Color.RED);
        
        getChildren().addAll(circle, line1, line2);
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
    private Pane content;
    private Pane dView;

    @Override
    public void start(Stage stage) {

        // create some content
        MyCircle circle = new MyCircle(100, 100, 30);
        MyCircle circle2 = new MyCircle(150, 120, 40);
        MyCircle circle3 = new MyCircle(200, 140, 50);
        content = new Pane(circle, circle2, circle3);
        content.setManaged(false);

        // add the content to a DrawingView for scaling and panning
        dView = new Pane(content);
        dView.setManaged(false);
        
        dView.setOnScroll(e -> {
            //System.err.println(e);

            double delta = 0.5;
            if (e.getDeltaY() < 0) {
                delta = - 0.5;
            }
            double newScale = scaleFactor + delta;

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
        Group mainGroup = new Group(dView, boundVisuals);

        Scene mainScene = new Scene(mainGroup, 800, 600);
        stage.setScene(mainScene);
        stage.show();
    }

    private void updateBoundVisuals() {
        boundVisuals.getChildren().clear();
        
        Bounds cb = content.getBoundsInParent();
        Rectangle cbr = new Rectangle(cb.getMinX(), cb.getMinY(), cb.getWidth(), cb.getHeight());
        cbr.setStroke(Color.BLUE);
        cbr.setFill(null);
        String coords = String.format("%s/%s %sx%s",  cb.getMinX(), cb.getMinY(), cb.getWidth(), cb.getHeight());
        Text t1 = new Text(cb.getMinX() + cb.getWidth(), cb.getMinY() + cb.getHeight() - 20, coords);
        t1.setStroke(Color.BLUE);

        Bounds dv = dView.getBoundsInParent();
        Rectangle dvr = new Rectangle(dv.getMinX(), dv.getMinY(), dv.getWidth(), dv.getHeight());
        dvr.setStroke(Color.RED);
        dvr.setFill(null);
        coords = String.format("%s/%s %sx%s",  dv.getMinX(), dv.getMinY(), dv.getWidth(), dv.getHeight());
        Text t = new Text(dv.getMinX() + dv.getWidth(), dv.getMinY() + dv.getHeight(), coords);
        t.setStroke(Color.RED);

        Point2D m = boundVisuals.sceneToLocal(new Point2D(10, 10));
        Line line1 = new Line(m.getX()-5, m.getY(), m.getX()+5, m.getY());
        line1.setStroke(Color.RED);
        Line line2 = new Line(m.getX(), m.getY()-5, m.getX(), m.getY()+5);
        line2.setStroke(Color.RED);

        boundVisuals.getChildren().addAll(cbr, dvr, t1, t, line1, line2);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }
}
