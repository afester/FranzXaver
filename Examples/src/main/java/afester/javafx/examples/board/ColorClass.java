package afester.javafx.examples.board;

public enum ColorClass {
    BOTTOMTRACE("Bottom Trace"), BOTTOMPAD("Bottom Pad"), BOTTOMBOARD("Bottom Board"), BOTTOMPIN("Bottom Pin"),
    TOPTRACE("Top Trace"), TOPPAD("Top Pad"), TOPBOARD("Top Board"), TOPPIN("Top Pin");

    private final String name;

    ColorClass(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
