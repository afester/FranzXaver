package afester.javafx.components;

import com.sun.javafx.scene.control.behavior.BehaviorBase;  // TODO: Private API!
import com.sun.javafx.scene.control.behavior.KeyBinding;

import java.util.List;

// Behavior is the controller for a control - means, how the control internally
// behaves when some action is performed.
// Example: enable/disable a segment of a multi segment digit  when clicking on it!
public class MultiSegmentPanelBehavior extends BehaviorBase<MultiSegmentPanel> {

    public MultiSegmentPanelBehavior(MultiSegmentPanel control, List<KeyBinding> keyBindings) {
        super(control, keyBindings);
        // control.setOnMouseClicked(value);
    }
}
