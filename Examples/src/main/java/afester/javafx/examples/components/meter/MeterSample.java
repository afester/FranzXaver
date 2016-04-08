package afester.javafx.examples.components.meter;

import afester.javafx.components.Meter;
import afester.javafx.examples.Example;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


@Example(desc = "Analog meter", 
         cat  = "FranzXaver")
public class MeterSample extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    
    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        
        Meter theMeter = new Meter();
        theMeter.setUnitText("V");

        Slider valueSlider = new Slider(0.0, 1.5, 0.0);
        valueSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            double val = newValue.doubleValue();
            if (val > 1.0) {
                theMeter.setValue(1.0);
            } else {
                theMeter.setValue(val);
            }
        } );

        VBox mainGroup = new VBox();
        mainGroup.getChildren().addAll(theMeter, valueSlider);
        mainGroup.setSpacing(10);
        mainGroup.setPadding(new Insets(10, 10, 10, 10));

        Scene scene = new Scene(mainGroup);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
