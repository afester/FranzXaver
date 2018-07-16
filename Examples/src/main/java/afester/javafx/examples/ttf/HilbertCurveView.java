package afester.javafx.examples.ttf;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HilbertCurveView extends Application {
 
    public static void main(String[] args) {
        // The following properties are required on Linux for improved text rendering
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");
        launch(args);
    }

    private Pane canvas = new Pane();
    private Path path = new Path();     // the hilbert curve
    private Font idxFont = new Font("Sans", 8.0);
    private int hilbertIndex = 0;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Font Generator");

        canvas.setStyle("-fx-background-color: white;");
        canvas.setPrefSize(400, 400);

        VBox layout = new VBox();
        layout.setSpacing(10);

        HBox controls = new HBox();
        controls.setSpacing(10);
        controls.getChildren().add(new Text("Iteration:"));
        TextField numIters = new TextField();
        controls.getChildren().add(numIters);
        Button updateBtn = new Button("Update");
        updateBtn.setOnAction(e -> {
            int depth = Integer.parseInt(numIters.getText());
            canvas.getChildren().clear();
            drawRects(depth, 0, 0, 400, 400);
            path = new Path();
            canvas.getChildren().add(path);
            hilbertIndex = 0;
            hilbert(0, 0, 400,  0,  0,  400,  depth, Color.BLACK);
        });
        controls.getChildren().add(updateBtn);

        layout.getChildren().add(canvas);
        layout.getChildren().add(controls);

        Scene scene  = new Scene(layout);

        stage.setScene(scene);
        stage.show();
    }


    private void drawRects(int iteration, float x, float y, float w, float h) {

        Rectangle r = new Rectangle(x, y, w, h);
        r.setFill(null);
        r.setStroke(Color.LIGHTGRAY);
        canvas.getChildren().add(r);

        if (iteration > 0) {
            drawRects(iteration-1, x, y, w/2, h/2);
            drawRects(iteration-1, x+w/2, y, w/2, h/2);
            drawRects(iteration-1, x, y+h/2, w/2, h/2);
            drawRects(iteration-1, x+w/2, y+h/2, w/2, h/2);
        }
    }

    // http://www.fundza.com/algorithmic/space_filling/hilbert/basics/index.html
    private void hilbert(float x, float y, float xi, float xj, float yi, float yj, int n, Color col) {
        /* x and y are the coordinates of the bottom left corner */
        /* xi & xj are the i & j components of the unit x vector of the frame */
        /* similarly yi and yj */
        
        if (n <= 0) {
            System.err.printf("%s %s %s %s %s\n", xi, xj, yi, yj, n);

            float hilbertX = x + (xi + yi)/2;
            float hilbertY = (y + (xj + yj)/2);

            if (path.getElements().size() == 0) {
                MoveTo moveTo = new MoveTo(hilbertX, 400 - hilbertY);
                path.getElements().add(moveTo);
            } else {
                LineTo lineTo = new LineTo(hilbertX, 400 - hilbertY);
                path.getElements().add(lineTo);
            }

            Circle c = new Circle(hilbertX, 400 - hilbertY, 3.0);
            c.setFill(col);
            canvas.getChildren().add(c);

            // add a text in the lower left corner
            float dx = Math.abs((xi + xj) / 2);
            float dy = Math.abs((yi + yj) / 2);
            Text t = new Text(hilbertX - dx, hilbertY + dy, "" + hilbertIndex++);
            t.setFont(idxFont);
            canvas.getChildren().add(t);
        } else {
           // divide the current square into 4 sub squares
           hilbert(x,           y,           yi/2, yj/2,  xi/2,  xj/2, n-1, Color.RED);
           hilbert(x+xi/2,      y+xj/2 ,     xi/2, xj/2,  yi/2,  yj/2, n-1, Color.LIGHTGREEN);
           hilbert(x+xi/2+yi/2, y+xj/2+yj/2, xi/2, xj/2,  yi/2,  yj/2, n-1, Color.BLUE);
           hilbert(x+xi/2+yi,   y+xj/2+yj,  -yi/2,-yj/2, -xi/2, -xj/2, n-1, Color.VIOLET);
        }
    }
}
