package afester.javafx.components;

import javafx.event.Event;
import javafx.event.EventType;

public class DigitChangeEvent extends Event{

    private final static EventType<DigitChangeEvent> EVENT_TYPE = new EventType<>("DIGIT_CHANGE_EVENT");

    public DigitChangeEvent(FourteenSegment digit) {
        super(digit, null, EVENT_TYPE);
    }
}
