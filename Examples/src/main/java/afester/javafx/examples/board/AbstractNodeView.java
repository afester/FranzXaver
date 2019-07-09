package afester.javafx.examples.board;

import afester.javafx.examples.board.model.AbstractNode;
import javafx.geometry.Point2D;
import javafx.scene.Group;


public abstract class AbstractNodeView extends Group implements Interactable {

    protected AbstractNode node;

    public AbstractNodeView(AbstractNode node) {
        // setMouseTransparent(false);
        this.node = node;
    }

    @Override
	public Point2D getPos() {
	    return node.getPosition();
    }


//	@Override
//	public void setSelected(boolean isSelected) {
//		if (isSelected) {
//			setFill(Color.DARKRED);
//		} else {
//			setFill(null);
//		}
//	}
//
//    public void setSelected(boolean isSelected, Color col) {
//        if (isSelected) {
//            setFill(Color.BLUE); // col);
//        } else {
//            setFill(null);
//        }
//    }


//    public void setPos(Point2D snapPos) {
//        setCenterX(snapPos.getX());
//        setCenterY(snapPos.getY());
//        moveTraces2(snapPos.getX(), snapPos.getY());
//    }

//    /**
//     * @return A list of all edges which are connected to this node.
//     */
//    public List<AbstractWire> getEdges() {
//        List<AbstractWire> result = new ArrayList<>();
//
//        result.addAll(traceStarts);
//        result.addAll(traceEnds);
//
//        return result;
//    }


//    /**
//     * From a collection of nodes, get the one which is nearest to this one.
//     *
//     * @param nodeList The list of nodes from which to get the nearest one.
//     * @return The node which is the nearest to this one.
//     */
//    public AbstractNode getNearestNode(List<AbstractNode> nodeList) {
//        double minDist = Double.MAX_VALUE;
//        AbstractNode result = null;
//        for (AbstractNode node: nodeList) {
//            double dist = node.getConnectPosition().distance(getPos());
//            if (dist < minDist) {
//                result = node;
//                minDist = dist;
//            }
//        }
//
//        return result;
//    }
//
//    public Net getNet() {
//        return net;
//    }
}
