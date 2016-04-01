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
