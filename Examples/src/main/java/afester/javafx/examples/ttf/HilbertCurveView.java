package afester.javafx.examples.ttf;

import javafx.application.Application;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;


// http://www.soc.napier.ac.uk/~andrew/hilbert.html
// http://www.fundza.com/algorithmic/space_filling/hilbert/basics/index.html
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
        canvas.setPrefSize(500, 500);

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
            drawRects(depth, 50, 0, 400, 400);
            path = new Path();
            canvas.getChildren().add(path);
            hilbertIndex = 0;
            hilbert(0, 0,  400, 0,  0, 400,  depth, Color.BLACK);
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
        r.setStroke(Color.DARKGRAY);
        canvas.getChildren().add(r);

        final int rowCols = (1 << iteration);
        final float dc = w / rowCols;

        float xpos = x;
        float ypos = y;
        for (int i = 1;  i < rowCols;  i++) {
            xpos += dc;
            ypos += dc;

            Line lh = new Line(xpos, y, xpos, y+h);
            lh.setStroke(Color.LIGHTGRAY);
            canvas.getChildren().add(lh);

            Line lv = new Line(x, ypos, x+w, ypos);
            lv.setStroke(Color.LIGHTGRAY);
            canvas.getChildren().add(lv);
        }

        xpos = x;
        ypos = y;
        for (int i = 0;  i < rowCols;  i++) {
            Text t = new Text(xpos+ dc/2, y + h, "" + (i + 1));
            t.setFont(idxFont);
            t.setTextOrigin(VPos.TOP);
            canvas.getChildren().add(t);

            t = new Text(x - 10, ypos + dc/2, "" + (rowCols - i));
            t.setTextAlignment(TextAlignment.RIGHT);
            t.setFont(idxFont);
            canvas.getChildren().add(t);

            xpos += dc;
            ypos += dc;
        }

//        if (iteration > 0) {
//            drawRects(iteration-1,       x,     y, w/2, h/2);
//            drawRects(iteration-1,   x+w/2,     y, w/2, h/2);
//            drawRects(iteration-1,       x, y+h/2, w/2, h/2);
//            drawRects(iteration-1,   x+w/2, y+h/2, w/2, h/2);
//        }
    }

    private void hilbert(float x, float y, 
                         float xi, float xj, 
                         float yi, float yj, 
                         int n, Color col) {
        /* x and y are the coordinates of the bottom left corner */
        /* xi & xj are the i & j components of the unit x vector of the frame */
        /* similarly yi and yj */
        
        if (n <= 0) {
            float hilbertX = x + (xi + yi)/2;
            float hilbertY = (y + (xj + yj)/2);
            hilbertX += 50;
            hilbertY = 400 - hilbertY;

            if (path.getElements().size() == 0) {
                MoveTo moveTo = new MoveTo(hilbertX, hilbertY);
                path.getElements().add(moveTo);
            } else {
                LineTo lineTo = new LineTo(hilbertX, hilbertY);
                path.getElements().add(lineTo);
            }

            Circle c = new Circle(hilbertX, hilbertY, 3.0);
            c.setFill(col);
            canvas.getChildren().add(c);

            // add a text in the lower left corner
            float dx = Math.abs((xi + xj) / 2);
            float dy = Math.abs((yi + yj) / 2);
            Text t = new Text(hilbertX - dx + 4, hilbertY + dy -4, "" + hilbertIndex++);
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
