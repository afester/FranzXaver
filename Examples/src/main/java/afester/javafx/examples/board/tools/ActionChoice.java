package afester.javafx.examples.board.tools;

import afester.javafx.components.ToolbarToggleButton;
import javafx.scene.image.Image;


public class ActionChoice<T> {


    protected String title;
    private String description;
    protected Image icon;
    private T value;

    public ActionChoice(T value, String title, String description, Image icon) {
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.value = value;
    }

    public ToolbarToggleButton getToolbarButton() {
        ToolbarToggleButton result = new ToolbarToggleButton(title, icon);
//        toolbarButtons.add(result);
//
//        result.disableProperty().bind(enabledProperty().not());
//
//        result.setSelected(isSelected());
//        result.selectedProperty().addListener((obj, oldValue, newValue) -> {
//            this.setSelected(newValue);
//        });
//
        return result;
    }

    public T getValue() {
        return value;
    }
}
