package afester.javafx.examples.components.sevensegment;

import afester.javafx.components.SevenSegmentPanel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class SegmentDisplay extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {

        SevenSegmentPanel s7Panel = new SevenSegmentPanel(10, 0);
        s7Panel.setOffColor(  new Color(0x1c / 255.0, 0x23 / 255.0, 0x23 / 255.0, 1.0));
        s7Panel.setOnColor(   new Color(0x8E / 255.0, 0xd2 / 255.0, 0xd5 / 255.0, 1.0));
        s7Panel.setPanelColor(new Color(0x17 / 255.0, 0x1B / 255.0, 0x1A / 255.0, 1.0));
        s7Panel.setText("FranzXaver");

        VBox mainGroup = new VBox();
        mainGroup.getChildren().add(s7Panel);

        Scene scene = new Scene(mainGroup);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
