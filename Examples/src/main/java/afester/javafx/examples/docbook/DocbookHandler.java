package afester.javafx.examples.docbook;

public interface DocbookHandler {

//    @Deprecated
//    void addParagraph(String content, String string, int listLevel);
//@Deprecated
  //  void addCode(String content, String language);

    void addImage(String imagePath);

    void addFormula(String formula);

    void addFragment(String content, TextStyle tStyle, ParStyle paraStyle, int listLevel, boolean bullets);
}
