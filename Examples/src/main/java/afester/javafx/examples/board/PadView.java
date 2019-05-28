package afester.javafx.examples.board;

import afester.javafx.examples.board.model.Pad;
import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * A Pad is a junction which refers to a specific pin of a Part.
 */
public class PadView extends AbstractNodeView {

    private Pad pad;

    /**
     * Creates a new PadView.
     *
     */
    public PadView(Pad pad1) {
        super(pad1);
        this.pad = pad1;

        // Group result = new Group();
        //view = new Group();

        Shape pad = new Circle(getPos().getX(), getPos().getY(), 0.7); // drill*2);
        pad.setFill(Color.WHITE);
        pad.setStroke(Color.BLACK);
        pad.setStrokeWidth(0.6);

        Text padName = new Text(getPos().getX(), getPos().getY(), this.pad.getPinNumber());

        // TODO: The rendered text is messed up if the size is too small!
        // A possible solution seems to be to keep the text size larger and 
        // apply an appropriate scaling (and translation) to the Text node
        Font theFont = Font.font("Courier", 10.0);
        padName.setScaleX(0.1);
        padName.setScaleY(0.1);
        padName.setTranslateX(-3);
        padName.setFont(theFont);
        padName.setFill(Color.RED);
        padName.setTextOrigin(VPos.CENTER);

        getChildren().addAll(pad, padName);
    }

///*****************************/
//    
//
//    Node partNode = null;
//    @Override
//    public Point2D getPos() {
//        if (partNode != null) {
//            return partNode.localToParent(getCenterX(), getCenterY());
//        }
//        return new Point2D(0, 0); 
//    }
///*****************************/



//    @Override   // DEBUG ONLY
    public void setSelected(boolean isSelected, Color col) {
//        Shape pad = (Shape) view.getChildrenUnmodifiable().get(0);  // HACK
//        if (isSelected) {
//            pad.setFill(col);
//        } else {
//            pad.setFill(null);
//        }
    }

//    /**
//     * Returns a unique pad id, consisting of the part number and the pin number
//     *
//     * @return A board-unique pad id in the form "partName$pinNumber"
//     */
//    public String getPadId() {
//        return part.getName() + "$";
//    }
//
//    @Override
//    public int hashCode() {
//        final String key = pad.getPadId();
//
//        final int prime = 31;
//        int result = 1;
//        result = prime * result + key.hashCode();
//        return result;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj)
//            return true;
//        if (obj == null)
//            return false;
//        if (getClass() != obj.getClass())
//            return false;
//        Pad other = (Pad) obj;
//        
//        final String key = getPadId();
//
//        if (!key.equals(other.getPadId()))
//            return false;
//        return true;
//    }

    @Override
    public String getRepr() {
        return "Pad: " + pad.getPadId();
    }


    @Override
    public String toString() {
        return String.format("PadView[pad=%s]", pad); 
    }

    @Override
    public void setSelected(boolean isSelected) {
        // TODO Auto-generated method stub
        
    }
}
