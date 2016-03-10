package afester.javafx.examples;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Example {

    /**
     * @return A description of this example.
     */
    String desc();

    /**
     * @return The category into which this example shall be grouped.
     */
    String cat();
}
