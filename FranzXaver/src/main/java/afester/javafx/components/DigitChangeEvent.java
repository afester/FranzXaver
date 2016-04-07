package afester.javafx.components;

import javafx.event.Event;
import javafx.event.EventType;

public class DigitChangeEvent extends Event {

    private static final long serialVersionUID = -8487702664839335663L;
    
    private static final EventType<DigitChangeEvent> EVENT_TYPE = 
                                            new EventType<>("DIGIT_CHANGE_EVENT");

    public DigitChangeEvent(MultiSegment digit) {
        super(digit, null, EVENT_TYPE);
    }
}
