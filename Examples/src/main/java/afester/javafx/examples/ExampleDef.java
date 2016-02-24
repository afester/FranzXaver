package afester.javafx.examples;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ExampleDef {

    private String className;
    private String description;

    /**
     * Creates a new example definition.
     *
     * @param className  The name of the class which implements the example.
     * @param description  The description of the example.
     */
    public ExampleDef(String className, String description) {
        this.className = className;
        this.description = description;
    }

    
    /**
     * @return The fully qualified class name of the example which is defined by 
     *         this example definition.
     */
    public String getClassName() {
        return className;
    }

    /**
     * @return The description of the example which is defined by this example definition.
     */
    public String getDescription() {
        return description;
    }


    /**
     * Launches the example which is defined by this example definition.
     */
    public void run() {
        try {
            Class<?> clazz = Class.forName(className);

            // Runnable example = (Runnable) clazz.newInstance();
            // example.run();

            Object example = clazz.newInstance();
            Method runMethod = clazz.getMethod("run");
            runMethod.invoke(example);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return String.format("ExampleDef[class=%s, desc=%s]", className, description);
    }

}
