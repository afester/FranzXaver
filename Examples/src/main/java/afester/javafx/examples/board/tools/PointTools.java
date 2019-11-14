package afester.javafx.examples.board.tools;

import java.util.Iterator;
import java.util.function.BiConsumer;

import javafx.geometry.Point2D;

public class PointTools {

    /**
     * 
     * @param <T>
     * @param iterable
     * @param consumer
     */
    public static <T> void pointIterator(Iterable<T> iterable, BiConsumer<T, T> consumer) {
        Iterator<T> it = iterable.iterator();
        while(it.hasNext()) {
            T first = it.next();
            if(!it.hasNext()) return;
            T second = it.next();
            consumer.accept(first, second);
        }
    }


    /**
     * 
     * @param iterable
     * @param consumer
     */
    public static void lineIterator(Iterable<Double> iterable, BiConsumer<Point2D, Point2D> consumer) {
        Iterator<Double> it = iterable.iterator();

        if(!it.hasNext()) return;
        final Double firstX = it.next();
        if(!it.hasNext()) return;
        final Double firstY = it.next();

        Double x1 = firstX;
        Double y1 = firstY;
        while(it.hasNext()) {
            Double x2 = it.next();
            if(!it.hasNext()) return;
            Double y2 = it.next();

            consumer.accept(new Point2D(x1, y1), new Point2D(x2, y2));
            x1 = x2;
            y1 = y2;
        }

        // close the polygon (THIS SEEMS WRONG - at least two points need to exist!)
        consumer.accept(new Point2D(x1, y1), new Point2D(firstX, firstY));
    }

    /**
     * 
     * @param iterable
     * @param consumer
     */
    public static void linePointsIterator(Iterable<Point2D> iterable, BiConsumer<Point2D, Point2D> consumer) {
        Iterator<Point2D> it = iterable.iterator();

        if(!it.hasNext()) return;
        final Point2D first = it.next();
        
        Point2D p1 = first;
        while(it.hasNext()) {
            Point2D p2 = it.next();

            consumer.accept(p1, p2);
            p1 = p2;
        }

        // close the polygon - need to check if at least two points are there!
        consumer.accept(p1, first);
    }

}
