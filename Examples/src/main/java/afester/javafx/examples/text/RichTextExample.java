package afester.javafx.examples.text;


import java.net.URL;

import afester.javafx.examples.animation.counter.AnimatedCounter;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
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

        // show the generated scene graph
        Scene scene = new Scene(rta);
        scene.getStylesheets().add(RichTextExample.class.getResource("richtext.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
