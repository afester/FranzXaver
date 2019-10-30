package afester.javafx.examples.controls;

import afester.javafx.examples.Example;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;



class BoundsRect extends Rectangle {

    protected BoundsRect() {
    }

    public BoundsRect(ReadOnlyObjectProperty<Bounds> boundsProperty, Color color) {
        setFill(null);
        setStroke(color);

        System.err.println("BOUNDS       :" + boundsProperty.getValue());
        boundsProperty.addListener((obj, o, newValue) -> {
                System.err.println("BOUNDS       :" + newValue);
                setBounds(newValue);
          });

        setBounds(boundsProperty.getValue());
    }


    protected void setBounds(Bounds sceneBounds) {
        setLayoutX(sceneBounds.getMinX());
        setLayoutY(sceneBounds.getMinY());
        setWidth(sceneBounds.getWidth());
        setHeight(sceneBounds.getHeight());
    }
}

class BoundsInParentOrLocalRect extends BoundsRect {


    public BoundsInParentOrLocalRect(Node node, Color color) {
        this(node, color, false);
    }
    
    
    public BoundsInParentOrLocalRect(Node node, Color color, boolean parentBounds) {
        setFill(null);
        setStroke(color);

        System.err.println("BOUNDS       :" + node.getBoundsInLocal());
        System.err.println("Parent BOUNDS:" + node.getBoundsInParent());

        if (parentBounds) {
            getStrokeDashArray().addAll(2.0, 2.0);


            node.boundsInParentProperty().addListener((obj, o, newValue) -> {
                System.err.println("BOUNDS       :" + node.getBoundsInLocal());
                System.err.println("Parent BOUNDS:" + node.getBoundsInParent());

                //Bounds sceneBounds = node. node.localToScene(newValue);
//                System.err.printf("%s: %s\n", color, sceneBounds);
                setBounds(newValue);
          });
            //Bounds sceneBounds = node.localToScene(node.getBoundsInParent());
            setBounds(node.getBoundsInParent());
        } else {
            
            node.boundsInLocalProperty().addListener((obj, o, newValue) -> {
                Bounds sceneBounds = node.localToScene(newValue);
                
                System.err.println("BOUNDS       :" + node.getBoundsInLocal());
                System.err.println("Parent BOUNDS:" + node.getBoundsInParent());
                System.err.println("Scene BOUNDS:" + sceneBounds);

//                System.err.printf("%s: %s\n", color, sceneBounds);
                setBounds(sceneBounds);
          });
            
            Bounds sceneBounds = node.localToScene(node.getBoundsInLocal());
            setBounds(sceneBounds);
        }
    }
}


class LayoutBoundsRect extends BoundsRect {

    public LayoutBoundsRect(Node node, Color color) {
        setFill(null);
        setStroke(color);

        System.err.println("Layout BOUNDS:" + node.getLayoutBounds());
        System.err.println("Parent BOUNDS:" + node.getBoundsInParent());
        System.err.println("Local  BOUNDS:" + node.getBoundsInLocal());

        node.layoutBoundsProperty().addListener((obj, o, newValue) -> {
            Bounds sceneBounds = newValue; // node.localToScene(newValue);

            System.err.println("Layout BOUNDS:" + newValue); // node.getBoundsInLocal());
            System.err.println("Parent BOUNDS:" + node.getBoundsInParent());
            System.err.println("Local  BOUNDS:" + node.getBoundsInLocal());

            setBounds(sceneBounds);
      });

      Bounds sceneBounds = node.getLayoutBounds(); // node.localToScene(node.getLayoutBounds());
      setBounds(sceneBounds);
    }
}


class ContentRoot extends Pane {

    public ContentRoot() {
//        setWidth(800);
//        setHeight(600);
//        setStyle("-fx-border-color: red; -fx-border-style: solid; -fx-border-width: 1px;");

        setManaged(false);  // Required to keep the size of the panel - otherwise the size is adjusted to the bounding box of its children!
    }

    @Override
    public String toString() {
        return String.format("ContentRoot", getId());
    }

}


@Example(desc = "ScrollPane",
         cat  = "JavaFX")
public class ScrollPaneExample extends Application {

    private double scaleFactor = 1.0;
    private static final double SCALE_STEP = Math.sqrt(1.3);
    private double mx = 0;
    private double my = 0;

    private boolean isPanning = false;
    private Cursor oldCursor;

    // TODO: Move to a central place and make it customizable
    private boolean isPanelAction(MouseEvent e) {
        return (e.isControlDown() && e.isShiftDown()); 
    }


    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX scrollpane example");

        Pane content = new Pane() {
            @Override
            public String toString() {
                return "ContentPane";
            }
        };
        content.resize(8000, 8000);

        content.setManaged(false);
//        RectangleObject r1 = new RectangleObject("R1", Color.RED, new Point2D(100, 100), 100, 50);
//        CircleObject r2 = new CircleObject("C1", Color.YELLOW, new Point2D(-50, -50), 100);
        CircleObject r2 = new CircleObject("C1", Color.YELLOW, new Point2D(0, 0), 100);
        content.getChildren().addAll(/*r1,*/ r2);

        Pane p = new Pane();
        p.setManaged(false);
        p.getChildren().addAll(content);

        ScrollPane scrollPaneNode = new ScrollPane();
        scrollPaneNode.setHbarPolicy(ScrollBarPolicy.ALWAYS);
        scrollPaneNode.setVbarPolicy(ScrollBarPolicy.ALWAYS);

        // rootNode.setPannable(true);

        // need to wrap the top level container into a Group to use visual bounds
        Group v = new Group(p);
        v.setManaged(false);
        scrollPaneNode.setContent(v);

        // the Pane also has the mouse listeners for pan and zoom in order to be able to catch them at any position 
        scrollPaneNode.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> { // System.err.println(e));
        // p.setOnMousePressed( e-> {
            if (isPanelAction(e)) {
                System.err.println("VIEW:" + e);

                mx = e.getX();
                my = e.getY();
                oldCursor = scrollPaneNode.getCursor();
                scrollPaneNode.setCursor(Cursor.CLOSED_HAND);
                isPanning = true;
            }
        });
        
        // NOTE: setOnMouseDragged event not fired - catched by ScrollPane
        // https://bugs.openjdk.java.net/browse/JDK-8119464
//        rootNode.setOnMouseDragged(e -> {
        scrollPaneNode.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> { // System.err.println(e));
        // p.setOnMouseDragged(e -> {
            if (isPanning) {
             //   System.err.println("VIEW:" + e);

                double dx = mx - e.getX();
                double dy = my - e.getY();
                mx = e.getX();
                my = e.getY();

                final double xpos = content.getLayoutX() - dx;
                final double ypos = content.getLayoutY() - dy;
                System.err.printf("NEW POS: %s/%s\n", xpos, ypos);
                content.setLayoutX(xpos);
                content.setLayoutY(ypos);
            } 
        });

        scrollPaneNode.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
        // p.setOnMouseReleased(e -> {
            if (isPanning) {
                scrollPaneNode.setCursor(oldCursor);
                isPanning = false;
            }
        });

        scrollPaneNode.setOnScroll(e-> {
            
            if (e.isControlDown()) {
                // System.err.println(e);

                // calculate the new scale factor
                double newScale;
                if (e.getDeltaY() < 0) {
                    newScale = scaleFactor / SCALE_STEP;
                }else {
                    newScale = scaleFactor * SCALE_STEP;
                }
                if (newScale > 0) {
                    scaleFactor = newScale;

                    // store current mouse coordinates as a reference point
                    final Point2D mPos = new Point2D(e.getSceneX(), e.getSceneY());       // scene coordinates of mouse
                    final Point2D mPosContent = content.sceneToLocal(mPos);               // position in content coordinates

                    // scale the content
                    content.setScaleX(scaleFactor);
                    content.setScaleY(scaleFactor);

                    // move the content so that the reference point is again at the mouse position
                    Point2D dviewPos = content.localToParent(mPosContent);  // reference point in dview coordinates
                    Point2D dviewMouse = scrollPaneNode.sceneToLocal(mPos);                // destination point
                    Point2D diff = dviewMouse.subtract(dviewPos);
                    
                    content.setLayoutX(content.getLayoutX() + diff.getX());
                    content.setLayoutY(content.getLayoutY() + diff.getY());
                }
            }
        });

        // show the generated scene graph
        // "If a resizable node (layout Region or Control) is set as the root, then the root's size will track the scene's size."

        StackPane rootNode = new StackPane();   // StackPane tries to center its children!
        Pane boundsNode = new Pane();
        boundsNode.setStyle("-fx-border-color: green; -fx-border-width: 1px;");
        boundsNode.setMouseTransparent(true);

        rootNode.getChildren().addAll(scrollPaneNode, boundsNode);
        Scene scene = new Scene(rootNode, 1000, 800);
        primaryStage.setScene(scene);
        primaryStage.show();

        // set up boundsTracking
        StackPane viewContent = (StackPane) v.getParent();
        StackPane viewRect = (StackPane) viewContent.getParent();
//        BoundsRect rb2 = new BoundsRect(p, Color.GREEN);
        BoundsRect rb3 = new BoundsInParentOrLocalRect(viewContent, Color.BLACK);
//        BoundsRect rb4 = new BoundsRect(viewRect, Color.MAGENTA);
//        BoundsRect rb5 = new BoundsRect(content, Color.BLUE);
//        BoundsRect rb6 = new BoundsRect(content, Color.BLACK, true);

        BoundsRect rb7 = new LayoutBoundsRect(v, Color.MAGENTA);
        BoundsRect rb8 = new BoundsRect(scrollPaneNode.viewportBoundsProperty(), Color.ORANGE);
        System.err.println(content);
        boundsNode.getChildren().addAll(/*rb2, */rb3, /*rb4, rb5, rb6,*/ rb7, rb8);
    }
}
