package afester.javafx.svg;

import javafx.animation.Transition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public final class StrokeDashOffsetTransition extends Transition {


    /**
     * The target shape of this {@code StrokeTransition}.
     * <p>
     * It is not possible to change the target {@code shape} of a running
     * {@code StrokeTransition}. If the value of {@code shape} is changed for a
     * running {@code StrokeTransition}, the animation has to be stopped and
     * started again to pick up the new value.
     */
    private ObjectProperty<Shape> shape;
    private static final Shape DEFAULT_SHAPE = null;

    public final void setShape(Shape value) {
        if ((shape != null) || (value != null /* DEFAULT_SHAPE */)) {
            shapeProperty().set(value);
        }
    }

    public final Shape getShape() {
        return (shape == null)? DEFAULT_SHAPE : shape.get();
    }

    public final ObjectProperty<Shape> shapeProperty() {
        if (shape == null) {
            shape = new SimpleObjectProperty<Shape>(this, "shape", DEFAULT_SHAPE);
        }
        return shape;
    }


    /**
     * The duration of this {@code StrokeDashOffsetTransition}.
     * <p>
     * It is not possible to change the {@code duration} of a running
     * {@code StrokeDashOffsetTransition}. If the value of {@code duration} is changed for
     * a running {@code StrokeDashOffsetTransition}, the animation has to be stopped and
     * started again to pick up the new value.
     * <p>
     * Note: While the unit of {@code duration} is a millisecond, the
     * granularity depends on the underlying operating system and will in
     * general be larger. For example animations on desktop systems usually run
     * with a maximum of 60fps which gives a granularity of ~17 ms.
     *
     * Setting duration to value lower than {@link Duration#ZERO} will result
     * in {@link IllegalArgumentException}.
     *
     * @defaultValue 400ms
     */
    private ObjectProperty<Duration> duration;
    private static final Duration DEFAULT_DURATION = Duration.millis(400);

    public final void setDuration(Duration value) {
        if ((duration != null) || (!DEFAULT_DURATION.equals(value))) {
            durationProperty().set(value);
        }
    }

    public final Duration getDuration() {
        return (duration == null)? DEFAULT_DURATION : duration.get();
    }

    public final ObjectProperty<Duration> durationProperty() {
        if (duration == null) {
            duration = new ObjectPropertyBase<Duration>(DEFAULT_DURATION) {

                @Override
                public void invalidated() {
                    try {
                        setCycleDuration(getDuration());
                    } catch (IllegalArgumentException e) {
                        if (isBound()) {
                            unbind();
                        }
                        set(getCycleDuration());
                        throw e;
                    }
                }

                @Override
                public Object getBean() {
                    return StrokeDashOffsetTransition.this;
                }

                @Override
                public String getName() {
                    return "duration";
                }
            };
        }
        return duration;
    }


    /**
     * Specifies the start dash offset value for this {@code StrokeDashOffsetTransition}.
     * <p>
     * It is not possible to change {@code fromValue} of a running
     * {@code StrokeDashOffsetTransition}. If the value of {@code fromValue} is changed
     * for a running {@code StrokeDashOffsetTransition}, the animation has to be stopped
     * and started again to pick up the new value.
     *
     * @defaultValue {@code null}
     */
    private DoubleProperty fromValue;
    private static final double DEFAULT_FROM_VALUE = Double.NaN;

    public final void setFromValue(double value) {
        if ((fromValue != null) || (!Double.isNaN(value) /* DEFAULT_FROM_VALUE */ )) {
            fromValueProperty().set(value);
        }
    }

    public final double getFromValue() {
        return (fromValue == null) ? DEFAULT_FROM_VALUE : fromValue.get();
    }

    public final DoubleProperty fromValueProperty() {
        if (fromValue == null) {
            fromValue = new SimpleDoubleProperty(this, "fromValue", DEFAULT_FROM_VALUE);
        }
        return fromValue;
    }

    /**
     * Specifies the stop dash offset for this {@code StrokeDashOffsetTransition}.
     * <p>
     * It is not possible to change {@code toValue} of a running
     * {@code StrokeDashOffsetTransition}. If the value of {@code toValue} is changed for
     * a running {@code StrokeDashOffsetTransition}, the animation has to be stopped and
     * started again to pick up the new value.
     *
     * @defaultValue {@code null}
     */
    private DoubleProperty toValue;
    private static final double DEFAULT_TO_VALUE = Double.NaN;

    public final void setToValue(double value) {
        if ((toValue != null) || (!Double.isNaN(value) /* DEFAULT_TO_VALUE */ )) {
            toValueProperty().set(value);
        }
    }

    public final double getToValue() {
        return (toValue == null) ? DEFAULT_TO_VALUE : toValue.get();
    }

    public final DoubleProperty toValueProperty() {
        if (toValue == null) {
            toValue = new SimpleDoubleProperty(this, "toValue", DEFAULT_TO_VALUE);
        }
        return toValue;
    }


    /**
     * The constructor of {@code StrokeDashOffsetTransition}
     *
     * @param duration The duration of the {@code StrokeDashOffsetTransition}
     * @param shape The {@code shape} which filling will be animated
     * @param fromValue The start value of the color-animation
     * @param toValue The end value of the color-animation
     */
    public StrokeDashOffsetTransition(Duration duration, Shape shape) {
        setDuration(duration);
        setShape(shape);
        setCycleDuration(duration);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void interpolate(double frac) {
        final double newDashOffset = 
                getFromValue() + frac * (getToValue() - getFromValue());

        //System.err.printf("INTERPOLATE: %s\n", getShape());
        //System.err.printf("             %s-%s / %s -> %s\n", getFromValue(), getToValue(), frac, newDashOffset);
        getShape().setStrokeDashOffset(newDashOffset);
    }    

}
