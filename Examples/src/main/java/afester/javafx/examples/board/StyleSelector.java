package afester.javafx.examples.board;

public enum StyleSelector {
    BOTTOMBOARD("Bottom Board"), BOTTOMPAD("Bottom Pad"), BOTTOMPIN("Bottom Pin"),
    BOTTOMTRACE_NORMAL("Bottom Trace normal"), BOTTOMTRACE_HIGHLIGHTED("Bottom Trace highlighted"), BOTTOMTRACE_SELECTED("Bottom Trace selected"), 
    BOTTOMAIRWIRE_NORMAL("Bottom Airwire normal"), BOTTOMAIRWIRE_HIGHLIGHTED("Bottom Airwire highlighted"), BOTTOMAIRWIRE_SELECTED("Bottom Airwire selected"), 
    BOTTOMBRIDGE_NORMAL("Bottom Bridge normal"), BOTTOMBRIDGE_HIGHLIGHTED("Bottom Bridge highlighted"), BOTTOMBRIDGE_SELECTED("Bottom Bridge selected"),

    TOPBOARD("Top Board"), TOPPAD("Top Pad"), TOPPIN("Top Pin"),
    TOPTRACE_NORMAL("Top Trace normal"), TOPTRACE_HIGHLIGHTED("Top Trace highlighted"), TOPTRACE_SELECTED("Top Trace selected"), 
    TOPAIRWIRE_NORMAL("Top Airwire normal"), TOPAIRWIRE_HIGHLIGHTED("Top Airwire highlighted"), TOPAIRWIRE_SELECTED("Top Airwire selected"), 
    TOPBRIDGE_NORMAL("Top Bridge normal"), TOPBRIDGE_HIGHLIGHTED("Top Bridge highlighted"), TOPBRIDGE_SELECTED("Top Bridge selected");

    private final String name;

    StyleSelector(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {return name;}
}
