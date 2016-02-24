package afester.javafx.examples;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class AllExamples extends Application {
   

    private List<ExampleDef> getExamples() {
        List<ExampleDef> result = new ArrayList<>();

        InputStream is = getClass().getResourceAsStream("examples.lst");
        BufferedReader bir = new BufferedReader(new InputStreamReader(is));

        String className = null;
        try {
            while ( (className = bir.readLine()) != null) {
                Class<?> clazz = Class.forName(className);
                Example[] annos = clazz.getAnnotationsByType(Example.class);
                if (annos.length > 0) {
                    Example exampleAnnotation = annos[0];
                    result.add(new ExampleDef(className, exampleAnnotation.value()));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        return result;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox mainGroup = new VBox();
        mainGroup.setPadding(new Insets(10));
        mainGroup.setSpacing(10);

        List<ExampleDef> examples = getExamples();
        for (ExampleDef ed : examples) {
            Button launcher = new Button(ed.getDescription());
            launcher.setOnAction(e -> ed.run());
            mainGroup.getChildren().add(launcher);
        }

        Scene scene = new Scene(mainGroup);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
