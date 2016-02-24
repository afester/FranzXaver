package afester.javafx.examples;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Example {

    /**
     * @return The value passed to the annotation.
     */
    String value();

}
