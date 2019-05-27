package afester.javafx.examples.board;

import afester.javafx.examples.board.model.Pad;
import javafx.scene.paint.Color;

/**
 * A Pad is a junction which refers to a specific pin of a Part.
 */
public class PadView extends AbstractNodeView {

    private Pad pad;

    /**
     * Creates a new PadView.
     *
     */
    public PadView(Pad pad) {
        super(pad);
        this.pad = pad;
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



    @Override   // DEBUG ONLY
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
}
