package afester.javafx.examples.board.view;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;


/**
 * The TraceGroup contains the visualization of all traces.
 * In addition to the Airwire- and Bridge Groups, it can have more than one
 * layer to allow for a nicer visualization of the Traces. 
 */
public class TraceGroup extends Group {

    private final Group layer1 = new Group();
    private final Group layer2 = new Group();
    private final List<TraceView> traceViews = new ArrayList<>(); // NOTE: TraceViews are not Nodes and hence are not part of the scene graph!

    // The color of the traces
    private final ObjectProperty<Color> color = new SimpleObjectProperty<Color>(Color.BLACK);
    public ObjectProperty<Color> colorProperty() { return color; }
    public Color getColor() { return color.get(); }
    public void setColor(Color newColor) { color.set(newColor); }

    // The width of the traces (in mm)
    private final DoubleProperty width = new SimpleDoubleProperty(1.0);
    public DoubleProperty widthProperty() { return width; }
    public Double getWidth() { return width.get(); }
    public void setWidth(Double newWidth) { width.set(newWidth); }

    public TraceGroup() {
        layer1.setId("layer1");
        layer2.setId("layer2");
        getChildren().addAll(layer1, layer2);

        color.addListener((obj, oldValue, newValue) -> {
            traceViews.forEach(traceView -> traceView.setStroke(newValue));
        });

        width.addListener((obj, oldValue, newValue) -> {
            traceViews.forEach(traceView -> traceView.setStrokeWidth(newValue.doubleValue()));
         });
    }

    public void addTrace(TraceView traceView) {
        traceViews.add(traceView);
        layer1.getChildren().addAll(traceView.theLine); // , traceView.gradientLine);
//        layer2.getChildren().addAll(traceView.theLine2);
    }

    public void removeTrace(TraceView traceView) {
        traceViews.remove(traceView);
        layer1.getChildren().remove(traceView.theLine);
 //       layer2.getChildren().remove(traceView.theLine2);
    }
    
    public List<TraceView> getTraceViews() {
        return traceViews;
    }
}
