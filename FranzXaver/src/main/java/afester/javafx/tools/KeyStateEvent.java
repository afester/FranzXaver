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

package afester.javafx.tools;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.KeyCode;

public class KeyStateEvent extends Event {

    private static final long serialVersionUID = -973185130826008148L;

    private static final EventType<KeyStateEvent> EVENT_TYPE = new EventType<>("KEY_CHANGE_EVENT");

    private KeyCode key;

    public KeyStateEvent(KeyCode activeKey) {
        super(null, null, EVENT_TYPE);
        this.key = activeKey;
    }
    
    
    public KeyCode getKeyCode() {
        return key;
    }

    @Override
    public String toString() {
        return "KeyChangeEvent[" + key + "]";
    }
}
