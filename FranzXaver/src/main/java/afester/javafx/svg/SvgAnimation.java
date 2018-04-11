/*
 * Copyright 2016 Andreas Fester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package afester.javafx.svg;

import java.util.Arrays;
import java.util.List;

import org.apache.batik.anim.dom.SVGOMAnimateElement;
import org.apache.batik.parser.ClockParser;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

public class SvgAnimation {

    private Node node;              // The JavaFX node to animate

    private String attributeName;   // The attribute to animate
    // private String attributeType;

    private double[] values;        // TODO: probably need to parameterize this type
    private double by;              // TODO: probably need to parameterize this type

    private Long begin;             // TODO: can also be a list of start times
    private Long end;               // TODO: can also be a list of end times
    private Long duration;          // duration in ms
    private int repeatCount = 0;   // how often to repeat - Timeline.INFINITE is infinite times 


    public SvgAnimation(Node node, SVGOMAnimateElement element) {
        this.node = node;

        // See https://www.w3.org/TR/SVG/animate.html#AnimateElement

        // <animate> animation attribute target attributes
        attributeName = element.getAttribute("attributeName");
        // attributeType = element.getAttribute("attributeType");      //  "CSS | XML | auto"

        // <animate> animation value attributes
        String valueList = element.getAttribute("values");             // alternatively, a discrete set of values to apply
        if (!valueList.equals("")) {
            // If a list of values is specified, any from, to and by attribute values are ignored.
            // By default, a simple linear interpolation is performed over the values, evenly spaced over the duration of the animation
            String[] theValues = valueList.split(";");
            values = new double[theValues.length];
            for (int idx = 0;  idx < theValues.length;  idx++) {
                values[idx] = Double.parseDouble(theValues[idx]);
            }
        } else {
            String from = element.getAttribute("from");             // start value for the attribute's value
            String to = element.getAttribute("to");                 // end value for the attribute's value
            values = new double[]{Double.parseDouble(from), Double.parseDouble(to) };
            by = Double.parseDouble(element.getAttribute("by"));    // delta value for the attribute's value
        }

        // <animate> animation timing attributes
        begin = parseTime(element.getAttribute("begin"));           // Semicolon separated list of start times (currently only single value supported)
        end = parseTime(element.getAttribute("end"));               // Semicolon separated list of end times (currently only single value supported)

        duration = parseTime(element.getAttribute("dur"));          // duration of the animation
        String repeatAttr = element.getAttribute("repeatCount");    // how often to repeat the animation
        if (repeatAttr.equals("indefinite")) {
            repeatCount = Timeline.INDEFINITE;
        } else {
            repeatCount = Integer.parseInt(repeatAttr);
        }

        //  String times = animate.getAttribute("times");           // Unsure - times is not an SVG animate attribute...
                                                                    // In Chrome, the sample runs also without this attribute.
    }


    /**
     * Returns the given time value in ms as a long value.
     *
     * @param timeValue The time value from an SVG animate attribute
     * @return The corresponding time value in ms
     */
    private float result;

    public Long parseTime(String timeValue) {
        if (timeValue.equals("")) {
            return null;
        }

        ClockParser p = new ClockParser(true);
        p.setClockHandler(e -> result = e );        // TODO: does the parser consider units like s and ms? 
                                                    // what is the result unit?
        p.parse(timeValue);
        return (long) (result * 1000);
    }


    /**
     * Normalizes the begin and end times of each animation.
     * The lowest begin time is considered an offset which is subtracted from all animations.
     * This is required because svg:animate obviously allows negative timeline values, while
     * JavaFX only allows positive timeline values.
     * 
     * @param animations
     */
    public static void normalize(List<SvgAnimation> animations) {
        long offset = Long.MAX_VALUE;

        for (SvgAnimation animation : animations) {
            offset = Math.min(offset, animation.begin);
            System.err.printf("%s - %s\n", offset, animation.begin);
        }
        if (offset != 0) {
            for (SvgAnimation animation : animations) {
                animation.begin -= offset;
                if (animation.end != null) {
                    animation.end -= offset;
                }
            }
        }
    }


    /**
     * Creates a JavaFX timeline from this SvgAnimation.
     * 
     * @return A JavaFX timeline object which corresponds to this SvgAnimation.
     */
    public Timeline createTimeline() {
        KeyValue kv;
        if (attributeName.equals("opacity")) {
            node.setOpacity(values[0]);                           // start value
            kv = new KeyValue(node.opacityProperty(), values[1]); // end value
        } else {
            throw new RuntimeException("Unknown property " + attributeName);
        }

        KeyFrame kf = new KeyFrame(new Duration(duration), kv);     // Duration of the animation
        Timeline t = new Timeline(kf);
        t.setDelay(new Duration(begin));                            // begin time
        t.setCycleCount(repeatCount);                               // repeatCount

        return t;
    }

    
    public String toString() {
        return String.format("SvgAnimation[attributeName=%s,\n"+
                             "             values=%s, by=%s,\n"+
                             "             begin=%s, end=%s, duration=%s, repeatCount=%s]",
                              attributeName, Arrays.toString(values), by, begin, end, duration, repeatCount);
    }
}
