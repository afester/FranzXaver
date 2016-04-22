package afester.javafx.examples.text;


import java.net.URL;

import afester.javafx.examples.animation.counter.AnimatedCounter;
import afester.javafx.examples.text.model.Document;
import afester.javafx.examples.text.model.Paragraph;
import afester.javafx.examples.text.model.TextFragment;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class RichTextExample extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        HBox mainLayout = new HBox();

/**************************************/
        RichTextArea rta = new RichTextArea();

        Document doc = new Document();

        Paragraph<String, String> p1 = new Paragraph<>("h1");
        p1.add(new TextFragment<String>("Big italic red text", "redItalic"));
        p1.add(new TextFragment<String>(" little bold blue text", "blueBold"));
        doc.add(p1);

        Paragraph<String, String> p2 = new Paragraph<>("p");
        p2.add(new TextFragment<String>("Lorem Ipsum Blafasel", "redItalic"));
        p2.add(new TextFragment<String>(" Lorem Ipsum", "blueBold"));
        URL imgUrl = AnimatedCounter.class.getResource("1.png");
        // p2.add(new ImageObject(imgUrl));
        p2.add(new TextFragment<String>(" Blafasel", "blueBold"));
        doc.add(p2);

        Paragraph<String, String> p3 = new Paragraph<>("java");
        p3.add(new TextFragment<String>("public class Test {\n   public static void "));
        p3.add(new TextFragment<String>("main", "keyword"));
        p3.add(new TextFragment<String>("(String[] args) {\n      System.err.println(\"Hello World\");\n   }\n}"));
        doc.add(p3);

        rta.setDocument(doc);
/**************************************/

        GridPane infoPane = new GridPane();

        infoPane.add(new Label("Current Paragraph:"), 0, 0);
        TextField pField = new TextField();
        pField.setEditable(false);
        infoPane.add(pField, 1, 0);

        infoPane.add(new Label("Current Column:"), 0, 1);
        TextField cField = new TextField();
        cField.setEditable(false);
        infoPane.add(cField, 1, 1);

        infoPane.add(new Label("Caret position:"), 0, 2);
        TextField cpField = new TextField();
        cpField.setEditable(false);
        infoPane.add(cpField, 1, 2);

        infoPane.add(new Label("Text length:"), 0, 3);
        TextField lField = new TextField();
        lField.setEditable(false);
        infoPane.add(lField, 1, 3);

        pField.textProperty().bind(Bindings.convert(rta.currentParagraphProperty()));
        cField.textProperty().bind(Bindings.convert(rta.caretColumnProperty()));
        cpField.textProperty().bind(Bindings.convert(rta.caretPositionProperty()));
        lField.textProperty().bind(Bindings.convert(rta.lengthProperty()));

/**************************************/

        mainLayout.getChildren().addAll(rta, infoPane);
        
        // show the generated scene graph
        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add(RichTextExample.class.getResource("richtext.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
