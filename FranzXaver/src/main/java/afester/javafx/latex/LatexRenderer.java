package afester.javafx.latex;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class LatexRenderer {

    private final java.awt.Color bgColor;
    private final java.awt.Color fgColor;
    private final TeXFormula formula = new TeXFormula();

    /**
     * Creates a new LatexRenderer which renders the LaTeX fragment in Black 
     * on transparent background. 
     */
    public LatexRenderer() {
        this(Color.BLACK, null);
    }

    /**
     * Creates a new LatexRenderer which renders the LaTeX fragment in a 
     * specific color on transparent background.
     * 
     * @param foreground The color to use for the foreground of the expression.
     */
    public LatexRenderer(Color foreground) {
        this(foreground, null);
    }


    /**
     * Creates a new LatexRenderer which renders the LaTeX fragment in a 
     * specified color on a specified background color.
     * 
     * @param foreground The foreground color to use.
     * @param background The background color to use.
     */
    public LatexRenderer(Color foreground, Color background) {
        if (background != null) {
            bgColor = new java.awt.Color((int) (background.getRed()*255),
                                         (int) (background.getGreen()*255), 
                                         (int) (background.getBlue()*255));
        } else {
            bgColor = null;
        }

        fgColor = new java.awt.Color((int) (foreground.getRed()*255),
                                     (int) (foreground.getGreen()*255), 
                                     (int) (foreground.getBlue()*255));
    }

    /**
     * Renders the given LaTeX fragment into an JavaFX Image.
     *
     * @param latex The LaTeX fragment to render.
     * 
     * @return An image with the rendered LaTeX fragment.
     */
    public Image render(String latex) {
        formula.setLaTeX(latex);
        TeXIcon icon = formula.new TeXIconBuilder().setStyle(TeXConstants.STYLE_DISPLAY).setSize(20).build();

        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        if (bgColor != null) {
            g2.setColor(bgColor);
            g2.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
        }

        icon.setForeground(fgColor);
        icon.paintIcon(null, g2, 0, 0);

        return SwingFXUtils.toFXImage(image, null);
    }
}
