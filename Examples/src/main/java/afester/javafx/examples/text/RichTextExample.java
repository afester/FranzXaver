package afester.javafx.examples.text;


import java.net.URL;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import afester.javafx.examples.animation.counter.AnimatedCounter;
import afester.javafx.examples.text.model.Document;
import afester.javafx.examples.text.model.Paragraph;
import afester.javafx.examples.text.model.StyledText;


public class RichTextExample extends Application {

    private final TextArea structureView = new TextArea();
    private final RichTextArea rta = new RichTextArea();

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        GridPane infoPane = createInfoPane(rta);
        HBox editLayout = new HBox();
        editLayout.getChildren().addAll(rta, infoPane);

        structureView.setFont(Font.font("Courier New"));
        structureView.setEditable(false);

        Tab tab = new Tab();
        tab.setText("Edit");
        tab.setClosable(false);
        tab.setContent(editLayout);

        Tab tab4 = new Tab();
        tab4.setText("View Document Structure");
        tab4.setClosable(false);
        tab4.setContent(structureView);
        tab4.setOnSelectionChanged(e -> activateStructureView(e));

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(tab, tab4);

/**************************************/

        Document doc = new Document();

        Paragraph<String, String> px1 = new Paragraph<>("p");
        px1.add(new StyledText<String>("Lorem Ipsum\rBlafasel", ""));
        Paragraph<String, String> px2 = new Paragraph<>("p");
        px2.add(new StyledText<String>("Lorem Ipsum\nBlafasel", ""));
        Paragraph<String, String> px3 = new Paragraph<>("p");
        px3.add(new StyledText<String>("Lorem Ipsum\u2029Blafasel", ""));
        doc.add(px1);
        doc.add(px2);
        doc.add(px3);

        
        
        Paragraph<String, String> p1 = new Paragraph<>("h1");
        p1.add(new StyledText<String>("Big italic red text", "redItalic"));
        p1.add(new StyledText<String>(" little bold blue text", "blueBold"));
        doc.add(p1);

        Paragraph<String, String> p2 = new Paragraph<>("p");
        p2.add(new StyledText<String>("Lorem Ipsum Blafasel", "redItalic"));
        p2.add(new StyledText<String>(" Lorem Ipsum", "blueBold"));
        URL imgUrl = AnimatedCounter.class.getResource("1.png");
        // p2.add(new ImageObject(imgUrl));
        p2.add(new StyledText<String>(" Blafasel", "blueBold"));
        doc.add(p2);

        Paragraph<String, String> p3 = new Paragraph<>("java");
        p3.add(new StyledText<String>("public class Test {\n   public static void ", ""));
        p3.add(new StyledText<String>("main", "keyword"));
        p3.add(new StyledText<String>("(String[] args) {\n      System.err.println(\"Hello World\");\n   }\n}", ""));
        doc.add(p3);

        rta.setDocument(doc);
/**************************************/

        // show the generated scene graph
        Scene scene = new Scene(tabPane);
        scene.getStylesheets().add(RichTextExample.class.getResource("richtext.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    

    private GridPane createInfoPane(RichTextArea rta) {
        GridPane infoPane = new GridPane();
        int row = 0;
        infoPane.add(new Label("Current Paragraph:"), 0, row);
        TextField pField = new TextField();
        pField.setEditable(false);
        infoPane.add(pField, 1, row++);

        infoPane.add(new Label("Current Column:"), 0, row);
        TextField cField = new TextField();
        cField.setEditable(false);
        infoPane.add(cField, 1, row++);

        infoPane.add(new Label("Text length:"), 0, row);
        TextField lField = new TextField();
        lField.setEditable(false);
        infoPane.add(lField, 1, row++);

        infoPane.add(new Label("Anchor:"), 0, row);
        TextField aField = new TextField();
        aField.setEditable(false);
        infoPane.add(aField, 1, row++);

        infoPane.add(new Label("Caret position:"), 0, row);
        TextField cpField = new TextField();
        cpField.setEditable(false);
        infoPane.add(cpField, 1, row++);

        infoPane.add(new Label("Selection:"), 0, row);
        TextField sField = new TextField();
        sField.setEditable(false);
        infoPane.add(sField, 1, row++);

        pField.textProperty().bind(Bindings.convert(rta.currentParagraphProperty()));
        cField.textProperty().bind(Bindings.convert(rta.caretColumnProperty()));
        cpField.textProperty().bind(Bindings.convert(rta.caretPositionProperty()));
        lField.textProperty().bind(Bindings.convert(rta.lengthProperty()));
        sField.textProperty().bind(Bindings.convert(rta.selectionProperty()));
        aField.textProperty().bind(Bindings.convert(rta.anchorProperty()));
        
        return infoPane;
    }


    private void activateStructureView(Event e) {
        Tab tab = (Tab) e.getSource();
        if (tab.isSelected()) {
            structureView.clear();
            Document<String, String> doc = rta.getDocument();
            for (Paragraph<String, String> p : doc.getParagraphs()) {
                structureView.appendText(p.toString() + "\n");
                for (StyledText<String> t : p.getFragments()) {
                    structureView.appendText("   " + t.toString() + "\n");
                }
            }
        }
    }
}
