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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

import java.util.Stack;

/**
 * This utility class can be used to track the currently pressed key.  
 * Differently from the JavaFX key press events, this class creates 
 * key press events whenever a new key effectively becomes active.
 * In particular, it does not deliver additional events which result 
 * from the keyboard's repeat feature.
 * Additionally, depending on how far the keyboard supports it, the KeyStateManager
 * also takes care of simultaneously pressed keys. For example, If the user performs
 * the following actions on the keyboard:
 *
 * <ul><li>Press CURSOR_DOWN</li>
 * <li>Press CURSOR_LEFT</li>
 * <li>Release CURSOR_DOWN (will not cause an event - CURSOR_LEFT is still active!)</li>
 * <li>Release CURSOR_LEFT</li>
 * </ul>
 * then the following events are delivered:
 *
 * <ul><li>KeyStateEvent(CURSOR_DOWN)   =&gt; now CURSOR_DOWN is active</li>
 * <li>KeyStateEvent(CURSOR_LEFT)   =&gt; now CURSOR_LEFT is active</li>
 * <li>KeyStateEvent(null)          =&gt; no key is active anymore.</li>
 * </ul>
 * The KeyStateManager does not yet support tracking more than one key at the same time -
 * even though all currently pressed keys are known inside the KeyStateManager.
 * So it should be easy to add a method which returns all currently pressed keys.
 */
public class KeyStateManager {

    private Stack<KeyCode> keys = new Stack<>();
    private KeyCode previousKey = null;

    // The property which holds the event handler.
    private ObjectProperty<EventHandler<KeyStateEvent>> keyChangedProperty = 
                                                    new SimpleObjectProperty<>();

    /**
     * Sets an event handler for key state events.
     *
     * @param handler The event handler to set.
     */
    public void setOnKeyChangeEvent(EventHandler<KeyStateEvent> handler) {
        onKeyChangedProperty().set(handler);
    }


    /**
     * @return The current event handler for key state events.
     */
    public final EventHandler<KeyStateEvent> getOnKeyChanged() {
        return onKeyChangedProperty().get();
    }


    /**
     * @return The property object which holds the key state event handler.
     */
    public final ObjectProperty<EventHandler<KeyStateEvent>> onKeyChangedProperty() { 
        return keyChangedProperty;
    }


    /**
     * Creates a new key state manager for a JavaFX scene.
     * The key state manager installs handlers for key press and key
     * release events so that the current state of a key can be observed. 
     *
     * @param scene The scene on which to install the key event handlers
     */
    public KeyStateManager(Scene scene) {
        // Handler for key press event
        scene.setOnKeyPressed(e-> {

            KeyCode pressedKey = e.getCode();
            if (!keys.contains(pressedKey)) {
                getOnKeyChanged().handle(new KeyStateEvent(pressedKey));
                keys.push(pressedKey);
                previousKey = pressedKey;
            }
        });

        // Handler for key release event
        scene.setOnKeyReleased(e -> {
            // the released key is of no interest.
            // only that key which will now become active again will be reported.
            KeyCode releasedKey = e.getCode();
            keys.remove(releasedKey);

            if (keys.empty()) {
                getOnKeyChanged().handle(new KeyStateEvent(null));
                previousKey = null;
            } else {
                KeyCode topKey = keys.peek();
                if (topKey != null && !topKey.equals(previousKey)) {
                    getOnKeyChanged().handle(new KeyStateEvent(topKey));
                    previousKey = topKey;
                }
            }

        });
    }

}
