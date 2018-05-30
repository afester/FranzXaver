package afester.javafx.examples.ttf;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.text.FontWeight;

@SuppressWarnings("serial")
class FontChangedEvent extends Event {

    private String fontFamily;
    private boolean isItalic;
    private FontWeight weight;
    private Integer size;

    public FontChangedEvent(String fontFamily, boolean isItalic, FontWeight weight, Integer size) {
        super(EventType.ROOT);
        this.fontFamily = fontFamily;
        this.isItalic = isItalic;
        this.weight = weight;
        this.size = size;
    }
    
    
    public String getFamily() {
        return fontFamily;
    }

    
    public FontWeight getWeight() {
        return weight;
    }

    
    public boolean isItalic() {
        return isItalic;
    }

    public Integer getSize() {
        return size;
    }


    @Override
    public String toString() {
        return String.format("FontChangedEvent[%s, %s, %s, %s]", fontFamily, isItalic, weight, size);
    }
}