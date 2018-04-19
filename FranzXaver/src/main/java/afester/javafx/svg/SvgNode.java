package afester.javafx.svg;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.Animation;
import javafx.scene.Group;

/**
 * The root node of a JavaFX node hierarchy created from an SVG document.
 */
public class SvgNode extends Group {

    private List<Animation> animations = new ArrayList<>();
    
    void addAnimation(Animation animation) {
        if (animation != null)  {
            animations.add(animation);
        }
    }
    
    
    /**
     * Starts the animations defined in this SvgNode.
     */
    public void startAnimations() {
        for (Animation a : animations) {
            a.play();
        }
    }
    

    /**
     * Stops the animations defined in this SvgNode.
     */
    public void stopAnimations() {
        for (Animation a : animations) {
            a.stop();
        }
    }
}
