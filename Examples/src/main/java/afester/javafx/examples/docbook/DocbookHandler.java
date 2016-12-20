package afester.javafx.examples.docbook;

public interface DocbookHandler {

//    void addTitle(int level, String title);

    void addParagraph(String content, String string, int listLevel);

    void addCode(String content, String language);

    void addImage(String imagePath);
}
