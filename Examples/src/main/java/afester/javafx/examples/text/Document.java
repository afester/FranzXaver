package afester.javafx.examples.text;

import java.util.ArrayList;
import java.util.List;

public class Document<S, PS> {

    private List<Paragraph<S, PS>> paragraphs = new ArrayList<>();

    public void add(Paragraph<S, PS> para) {
        paragraphs.add(para);
    }

    public List<Paragraph<S, PS>> getParagraphs() {
        return paragraphs;
    }

}
