package afester.javafx.examples.board;

public enum ColorClass {
    TRACE("Trace"), PAD("Pad");

    private final String name;

    ColorClass(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}