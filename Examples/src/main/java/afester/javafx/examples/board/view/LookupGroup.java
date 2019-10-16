package afester.javafx.examples.board.view;

import java.util.ArrayList;
import java.util.List;

import afester.javafx.examples.board.Interactable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;

/**
 * A LookupGroup is a JavaFX Group which has the ability to return all
 * nodes at a given location.
 */
public class LookupGroup extends Group {

    public List<Interactable> pickAll(Point2D pos) {
        final List<Interactable> result = new ArrayList<>(); 

        collectInteractablesRec(pos, this, result);

        return result;
    }
    
    


    private void collectInteractablesRec(Point2D mpos, Parent parent, List<Interactable> result) {
        parent.getChildrenUnmodifiable().forEach(child -> {
            if (child instanceof Interactable) {
                System.err.println("    CONTAINS: " + child + "/" + mpos);
                final Point2D pos = child.sceneToLocal(mpos);
                if (child.contains(pos)) {
                    System.err.println("    YES");
                    result.add((Interactable) child);
                }
            } else if (child instanceof Parent) {
                collectInteractablesRec(mpos, (Parent) child, result);
            }
        });
        
    }
}
