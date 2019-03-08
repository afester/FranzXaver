package afester.javafx.examples.board;

import afester.javafx.examples.Example;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

@Example(desc = "Game example: Bouncing ball", 
         cat  = "FranzXaver")
public class BoardExample extends Application {

    @Override
    public void start(Stage stage){
        
        Board b = new Board(160, 100);
        Group mainGroup = new Group();
        mainGroup.getChildren().addAll(b);

        //b.setOnMouseDragEntered(e -> System.err.println(e));
        //b.setOnMouseDragExited(e -> System.err.println(e));

        Scene mainScene = new Scene(mainGroup); // , sceneWidth + 10, viewHeight + 10);

        stage.setScene(mainScene);
        stage.sizeToScene();
        // stage.setResizable(false);
        stage.show();
    }
    

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }
}
