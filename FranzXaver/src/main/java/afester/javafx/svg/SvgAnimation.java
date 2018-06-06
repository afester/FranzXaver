/*
 * Copyright 2018 Andreas Fester
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.batik.anim.dom.SVGOMAnimateElement;
import org.apache.batik.anim.dom.SVGOMAnimateTransformElement;
import org.apache.batik.anim.dom.SVGOMAnimationElement;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.parser.ClockParser;
import org.w3c.dom.NodeList;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.StrokeTransition;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public class SvgAnimation {

    public Node node;              // The JavaFX node to animate
    private String href;           // Alternatively, the id of the JavaFX node to animate

    private String attributeName;   // The attribute to animate
    // private String attributeType;

    private String[] values;        // The raw values retrieved from the svg:animate element.
                                    // Depending on the attribute to animate, this can be a simple double value,
                                    // a color, a list of values, a list of tuples and probably more.  

    private double by;              // TODO: probably need to parameterize this type

    private Long begin;             // TODO: can also be a list of start times
    private Long end;               // TODO: can also be a list of end times
    private Long duration;          // duration in ms
    private int repeatCount = 0;    // how often to repeat - Timeline.INFINITE is infinite times 


    /**
     * Creates an SvgAnimation object from an <svg:animate> element.
     *
     * @param node
     * @param element
     */
    public SvgAnimation(Node node, SVGOMAnimateElement element) {
        this.node = node;

        // See https://www.w3.org/TR/SVG/animate.html#AnimateElement


        populateAnimationAttributes(element);
    }


    /**
     * Creates an SvgAnimation object from an <svg:animateTransform> element.
     * 
     * @param node
     * @param animateTransform
     */
    public SvgAnimation(Node node, SVGOMAnimateTransformElement element) {
        this.node = node;
        populateAnimationAttributes(element);

//        String href = animateTransform.getAttributeNS(SVGOMAnimateTransformElement.XLINK_NAMESPACE_URI, SVGOMAnimateTransformElement.XLINK_HREF_ATTRIBUTE);
//        String type = animateTransform.getAttribute(SVGOMAnimateTransformElement.SVG_TYPE_ATTRIBUTE);
//        String repeatCount = animateTransform.getAttribute(SVGOMAnimateTransformElement.SVG_REPEAT_COUNT_ATTRIBUTE);
        //        keyTimes=0; 0.8; 1
        //        dur=3s
        //        values=0 150 150; 0 150 150; 180 150 150
        //        repeatCount=indefinite
        //        xlink:href=#frame
        //        begin=0s

//        System.err.printf("TRANS: %s %s %s\n", href, type, repeatCount);
        // SvgAnimation animation = new SvgAnimation(node, animate);
    }

    private void populateAnimationAttributes(SVGOMAnimationElement element) {
        href = element.getAttributeNS(SVGOMAnimateTransformElement.XLINK_NAMESPACE_URI, SVGOMAnimateTransformElement.XLINK_HREF_ATTRIBUTE);

        // <animate> animation attribute target attributes
        attributeName = element.getAttribute(SVGOMAnimateElement.SVG_ATTRIBUTE_NAME_ATTRIBUTE);
        // attributeType = element.getAttribute("attributeType");      //  "CSS | XML | auto"

        // <animate> animation value attributes
        String valueList = element.getAttribute(SVGOMAnimateElement.SVG_VALUES_ATTRIBUTE);             // alternatively, a discrete set of values to apply
        if (!valueList.equals("")) {
            // If a list of values is specified, any from, to and by attribute values are ignored.
            // By default, a simple linear interpolation is performed over the values, evenly spaced over the duration of the animation
            values = valueList.split(";");  // each discrete value is separated by ';'
        } else {
            String from = element.getAttribute(SVGOMAnimateElement.SVG_FROM_ATTRIBUTE).trim();      // start value for the attribute's value
            if (from.equals("")) {                                  // from is optional
                from = null; // "0"; // 0 is wrong.
            }

            String to = element.getAttribute(SVGOMAnimateElement.SVG_TO_ATTRIBUTE).trim();          // end value for the attribute's value
            values = new String[] {from, to};

            String byAttr = element.getAttribute(SVGOMAnimateElement.SVG_BY_ATTRIBUTE);
            if (byAttr.equals("")) {                                // by is optional
                byAttr = "0";
            }

            by = Double.parseDouble(byAttr);    // delta value for the attribute's value
        }

        // <animate> animation timing attributes
        begin = parseTime(element.getAttribute(SVGOMAnimateElement.SVG_BEGIN_ATTRIBUTE));           // Semicolon separated list of start times (currently only single value supported)
                                                                    // This is the time where the animation should start - 
                // Currently only offset values are supported.
                // the offset describes the time within the timeline where the
                // animation should start at.
                // if the animated attribute is opacity, the duration is 3s, and
                // the begin value is 1s, the property would start with opacity=0.333
                // NOTE that this is different from the Timeline.delay property which delays the animation, but still starts
                // with the propertie's current value!

        end = parseTime(element.getAttribute(SVGOMAnimateElement.SVG_END_ATTRIBUTE));               // Semicolon separated list of end times (currently only single value supported)

        duration = parseTime(element.getAttribute(SVGOMAnimateElement.SVG_DUR_ATTRIBUTE));          // duration of the animation
        String repeatAttr = element.getAttribute(SVGOMAnimateElement.SVG_REPEAT_COUNT_ATTRIBUTE);    // how often to repeat the animation
        if (repeatAttr.equals("indefinite")) {
            repeatCount = Timeline.INDEFINITE;
        } else {
            repeatCount = Integer.parseInt(repeatAttr);
        }

        //  String times = animate.getAttribute("times");           // Unsure - times is not an SVG animate attribute...
                                                                    // In Chrome, the sample runs also without this attribute.

        // There is "keyTimes", though:
        // "A semicolon-separated list of time values used to control the pacing of the animation. 
        //  Each time in the list corresponds to a value in the ‘values’ attribute list, and defines 
        //  when the value is used in the animation function. Each time value in the ‘keyTimes’ list 
        // is specified as a floating point value between 0 and 1 (inclusive), representing a proportional 
        // offset into the simple duration of the animation element.
        // For animations specified with a ‘values’ list, the ‘keyTimes’ attribute if specified must have exactly 
        // as many values as there are in the ‘values’ attribute. For from/to/by animations, the ‘keyTimes’ 
        // attribute if specified must have two values.
    }



    private float result;
    /**
     * Returns the given time value in ms as a long value.
     *
     * @param timeValue The time value from an SVG animate attribute
     * @return The corresponding time value in ms
     */
    private Long parseTime(String timeValue) {
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
     * Creates a JavaFX Animation from this SvgAnimation.
     * 
     * @return A JavaFX Animation object which corresponds to this SvgAnimation.
     */
    public Animation createAnimation() {
        if (attributeName.equals("opacity")) {

            // for opacity, we can use a FadeTransition
            FadeTransition ft = new FadeTransition(new Duration(duration), node);
            ft.setInterpolator(Interpolator.LINEAR);
            ft.setFromValue(Double.parseDouble(values[0]));
            ft.setToValue(Double.parseDouble(values[1]));
            ft.setCycleCount(repeatCount);
            if (begin < 0) {
                // TODO: This works but is not yet completely clear why the begin time stamp needs to 
                // be mirrored instead of shifted by the duration offset
                ft.jumpTo(new Duration(-begin));    
            } else {
                ft.jumpTo(new Duration(begin));   
            }

            return ft;

        } else if (attributeName.equals("stroke-dashoffset")) {

            StrokeDashOffsetTransition sdot = new StrokeDashOffsetTransition(Duration.millis(duration), (Shape) node);
            sdot.setInterpolator(Interpolator.LINEAR);
            
            if (values[0] != null) {
                sdot.setFromValue(Double.parseDouble(values[0]));
            } else {
                sdot.setFromValue( ((Shape) node).getStrokeDashOffset());
            }
            sdot.setToValue(Double.parseDouble(values[1]));
            sdot.setCycleCount(repeatCount);
            if (begin != null) {
                if (begin < 0) {
                    sdot.jumpTo(new Duration(-begin));    
                } else {
                    sdot.jumpTo(new Duration(begin));   
                }
            }

            return sdot;

        } else if (attributeName.equals("stroke")) {

            StrokeTransition st = new StrokeTransition(Duration.millis(duration), (Shape) node);
            st.setInterpolator(Interpolator.LINEAR);
            st.setFromValue(Color.RED);   // values[0]
            st.setToValue(Color.GREEN);   // values[1]
            st.setCycleCount(repeatCount);
            if (begin != null) {
                if (begin < 0) {
                    st.jumpTo(new Duration(-begin));    
                } else {
                    st.jumpTo(new Duration(begin));   
                }
            }

            return st;

        } else if (attributeName.equals("points")) {
            System.err.println("points VALUES: " + Arrays.toString(values));
            // Polygon p = (Polygon) node;        // points animations apply to a Polygon

            // Animating the points of a polygon is not that trivial ... 
            // kv = new KeyValue(p.getPoints(), 1.0); // values[1]); // end value

            // return null;

        } else if (attributeName.equals("d")) {
            System.err.println("d VALUES: " + Arrays.toString(values));
            
            // An SVG path in the JavaFX scene graph is either a Group of shapes or a JavaFX SVGPath 
            // ...
            // Animating the path elements a Path is not that trivial ... 
            // kv = new KeyValue(p.getPoints(), 1.0); // values[1]); // end value

            // return null;

        } else if (attributeName.equals("transform")) {
            System.err.println("TRANSFORM: " + this);
        } else {
            throw new RuntimeException("Unknown property " + attributeName);
        }

        return null;
    }


    /**
     * Returns a list of SvgAnimation objects from the svg:animate element. 
     * 
     * @param node   The JavaFX node which shall be animated.
     * @param element The avg:animate element
     * 
     * @return A list of SvgAnimation objects which represent the svg:animate element.
     */
    public static List<SvgAnimation> getAnimations(Node node, SVGOMElement element) {
        List<SvgAnimation> result = new ArrayList<>();

        // get the animate child nodes for this element
        NodeList nl = element.getChildNodes();
        for (int idx = 0;  idx < nl.getLength();  idx++) {
            org.w3c.dom.Node child = nl.item(idx);
            if ("animate".equals(child.getLocalName())) {
                SVGOMAnimateElement animate = (SVGOMAnimateElement) child;
                SvgAnimation animation = new SvgAnimation(node, animate);
                result.add(animation);
            }
        }

        return result;
    }

    public static List<SvgAnimation> getAnimateTransforms(Node node, SVGOMElement element) {
        List<SvgAnimation> result = new ArrayList<>();

        // get the animate child nodes for this element
        NodeList nl = element.getChildNodes();
        for (int idx = 0;  idx < nl.getLength();  idx++) {
            org.w3c.dom.Node child = nl.item(idx);
            if ("animateTransform".equals(child.getLocalName())) {
                SVGOMAnimateTransformElement animateTransform = (SVGOMAnimateTransformElement) child;
                SvgAnimation animation = new SvgAnimation(node, animateTransform);
                result.add(animation);
            }
        }

        return result;
    }


    @Override
    public String toString() {
        return String.format("SvgAnimation[href=%s, attributeName=%s,\n"+
                             "             values=%s, by=%s,\n"+
                             "             begin=%s, end=%s, duration=%s, repeatCount=%s]",
                              node == null ? href : node, attributeName, Arrays.toString(values), by, begin, end, duration, repeatCount);
    }
}
