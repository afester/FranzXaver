package afester.javafx.components;

import java.util.List;

import javafx.scene.control.Control;

import com.sun.javafx.scene.control.behavior.BehaviorBase;  // TODO: Private API!

// Behavior is the controller for a control - means, how the control internally
// behaves when some action is performed.
// Example: enable/disable a segment of a multi segment digit  when clicking on it!
public class SevenSegmentPanelBehavior extends BehaviorBase<SevenSegmentPanel> {

    public SevenSegmentPanelBehavior(SevenSegmentPanel control, List keyBindings) {
        super(control, keyBindings);
        // control.setOnMouseClicked(value);
    }
}
