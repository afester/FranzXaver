/*
 * Copyright 2016 Andreas Fester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package afester.javafx.components;

import com.sun.javafx.scene.control.behavior.BehaviorBase;  // TODO: Private API!
import com.sun.javafx.scene.control.inputmap.InputMap;
//import com.sun.javafx.scene.control.behavior.KeyBinding;
import com.sun.javafx.scene.control.inputmap.KeyBinding;

import java.util.List;

// Behavior is the controller for a control - means, how the control internally
// behaves when some action is performed.
// Example: enable/disable a segment of a multi segment digit  when clicking on it!
public class MultiSegmentPanelBehavior extends BehaviorBase<MultiSegmentPanel> {

    public MultiSegmentPanelBehavior(MultiSegmentPanel control, List<KeyBinding> keyBindings) {
        super(control);
        // control.setOnMouseClicked(value);
    }

	@Override
	public InputMap<MultiSegmentPanel> getInputMap() {
		// TODO Auto-generated method stub
		return null;
	}
}
