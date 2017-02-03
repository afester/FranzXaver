package afester.javafx.examples.docbook;

public interface DocbookHandler {

//    @Deprecated
//    void addParagraph(String content, String string, int listLevel);
//@Deprecated
  //  void addCode(String content, String language);

    void addImage(String imagePath, int listLevel, boolean bullets);

    void addFormula(String formula, int listLevel, boolean bullets);

    void addFragment(String content, TextStyle tStyle, ParStyle paraStyle, int listLevel, boolean bullets);
    
    void addFragmentWithNewline(String content, TextStyle tStyle, ParStyle paraStyle, int listLevel, boolean bullets);
}
