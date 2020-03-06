package afester.javafx.examples.board.tools;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;




public class Action {

    public static class Separator extends Action {

        @Override
        public MenuItem getMenuItem() {
            return new SeparatorMenuItem();
        }
    }

    private String title;
    private String description;
    private Image icon;
    private EventHandler<ActionEvent> action;

    // property which enables or disables this action
    // ...


    private Action() {
    }

    public Action(String title, String description, Image icon, 
                  EventHandler<ActionEvent> action) {
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.action = action;
    }

    public Action(String title, String description,
                  EventHandler<ActionEvent> action) {
        this(title, description, null, action);
    }

    public Action(String title, EventHandler<ActionEvent> action) {
        this(title, null, null, action);
    }


    public MenuItem getMenuItem() {
        MenuItem result = new MenuItem(title);
        result.setOnAction(action);
        return result;
    }

    public MenuItem getToolbarButton() {
        return null;
    }

    public static Menu createMenu(String title, Action... actions){
        Menu result = new Menu(title);
        for (Action a : actions) {
            result.getItems().add(a.getMenuItem());
        }

        return result;
    }
}
