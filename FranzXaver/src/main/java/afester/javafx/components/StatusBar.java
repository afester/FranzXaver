package afester.javafx.components;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class StatusBar extends HBox {

    private final StringProperty text = new SimpleStringProperty();
    public StringProperty textProperty() { return text; }
    public String getText() { return text.get(); }
    public void setText(String value) { text.set(value); }

	public StatusBar() {
		Text textNode = new Text();
		getChildren().add(textNode);
		textNode.textProperty().bind(text);
	}

//         HBox statusBar = new HBox();
//    
//    statusBar.setBackground(new Background(new BackgroundFill(Color.BLUE, new CornerRadii(0), new Insets(0))));
}
