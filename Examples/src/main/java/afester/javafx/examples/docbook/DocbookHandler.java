package afester.javafx.examples.docbook;

public interface DocbookHandler {

//    void addTitle(int level, String title);

    void addParagraph(String content, String string);

    void addCode(String content, String language);
}
