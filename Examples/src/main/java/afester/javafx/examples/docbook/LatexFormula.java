package afester.javafx.examples.docbook;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.fxmisc.richtext.model.Codec;

import afester.javafx.latex.LatexRenderer;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LatexFormula<S> extends CustomObject<S> {

    private String formula;

    public LatexFormula() {
    }

    public LatexFormula(String formula, S style) {
        super(style);
        this.formula = formula;
    }

    /**
     * @return The latex expression which is used for this formula.
     */
    public String getFormula() {
        return formula;
    }

    @Override
    public CustomObject<S> setStyle(S style) {
        return new LatexFormula<>(formula, style);
    }

    private Node node = null;

    @Override
    public Node createNode() {
        if (node == null) {
            LatexRenderer lr = new LatexRenderer();
            Image image = lr.render(formula);
            node = new ImageView(image);
        }
        
        return node;
    }

    @Override
    public void encode(DataOutputStream os) throws IOException {
      Codec.STRING_CODEC.encode(os, formula);
      // styleCodec.encode(os, i.style);
    }

    @Override
    public String toString() {
        return String.format("LatexFormula[formula=%s]", formula);
    }

    @Override
    protected void decode(DataInputStream is) throws IOException {
        formula = Codec.STRING_CODEC.decode(is);
        System.err.println("   " + formula);
//         S style = styleCodec.decode(is);
    }
}
