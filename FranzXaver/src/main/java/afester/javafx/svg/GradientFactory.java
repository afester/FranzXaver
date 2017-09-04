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

import javafx.geometry.Point2D;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

import org.apache.batik.anim.dom.SVGOMAnimatedTransformList;
import org.apache.batik.anim.dom.SVGOMGradientElement;
import org.apache.batik.anim.dom.SVGOMLinearGradientElement;
import org.apache.batik.anim.dom.SVGOMRadialGradientElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGTransform;
import org.w3c.dom.svg.SVGTransformList;

import java.util.List;


public class GradientFactory {
    private static final Logger logger = LogManager.getLogger();

    private GradientPolicy gradientTransformPolicy = GradientPolicy.USE_SUPPORTED;

    /**
     * Creates a new GradientFactory.
     * The gradient transformation policy of the GradientFactory is set to 
     * GradientPolicy.USE_SUPPORTED.
     */
    public GradientFactory() {
    }


    /**
     * Sets the gradient transformation policy of the factory.
     * Valid values are:
     * <ul><li>GradientPolicy.USE_SUPPORTED - The coordinates
     *         for the gradient are transformed using only supported transformation
     *         elements (translation and rotation). This will lead to non-accurate
     *         rendering if the matrix contains skew and/or scaling.</li>
     *     <li>GradientPolicy.USE_AS_IS - The coordinates
     *         for the gradient are transformed using the complete transformation matrix.
     *         This will lead to non-accurate rendering if the matrix contains skew and/or 
     *         scaling.</li>
     *     <li>GradientPolicy.DISCARD - The coordinates are not
     *         transformed at all. This will lead to non-accurate rendering
     *         if the matrix is not the identity matrix.</li></ul>
     * 
     * @param policy The policy to use when gradient transformations are performed.
     */
    public void setTransformationPolicy(GradientPolicy policy) {
        this.gradientTransformPolicy = policy;
    }


    /**
     * Creates a JavaFX LinearGradient from an SVG linearGradient element.
     * Since JavaFX does not yet support gradient transformations, some fuzziness
     * is applied when the SVG gradient definition contains a gradient transformation
     * matrix. The fuzziness can be controlled through the GradientFactory's 
     * gradientTransformPolicy. See {@link #setTransformationPolicy(GradientPolicy)}
     * for more information.
     *
     * @param element The SVG gradient element.
     * @param stops The stops which have already been extracted from the SVG gradient child nodes.
     *
     * @return A JavaFX LinearGradient based on the SVG linear gradient element.
     */
    public LinearGradient createLinearGradient(SVGOMLinearGradientElement element, 
                                               List<Stop> stops) {
        double startX = element.getX1().getBaseVal().getValue();
        double startY = element.getY1().getBaseVal().getValue();
        double endX = element.getX2().getBaseVal().getValue();
        double endY = element.getY2().getBaseVal().getValue();

        /*
ATTENTION:
A gradientTransform in SVG does not transform the gradient parameter coordinates.
Instead, the gradient is generated and then the generated gradient is transformed.
These are two different things!
In case of a LinearGradient, assumed a gradient definition line with an angle of 45deg,
if the line coordinates are scaled horizontally, this would change the angle.
If the generated gradient image is scaled horizontally, this would not change the angle
(or at least in a different matter). Also, since the gradient flows in a 90deg
angle regarding the gradient vector, a skew transformation would never be possible
by just transforming start and end coordinates of the gradient.
         */

        SVGMatrix matrix = getGradientTransform(element);
        if (matrix != null) {
            switch (gradientTransformPolicy) {
              case DISCARD :
                  logger.warn("Discarding gradientTransform");
                  break;

              case USE_AS_IS :
              {
                  TransformationOperations to1 = TransformationOperations.getFromSvg(matrix);
                  if (to1.hasSkew() || to1.hasScale()) {
                      logger.warn(
                              "GradientTransform includes scale or skew - "
                            + "this is not yet supported! Rendering might be inaccurate.");
                      logger.debug(to1);
                  }
                  final double newX1 = matrix.getA() * startX + matrix.getB() * startY 
                                       + matrix.getC();
                  final double newY1 = matrix.getD() * startX + matrix.getE() * startY 
                                       + matrix.getF();
                  final double newX2 = matrix.getA() * endX + matrix.getB() * endY + matrix.getC();
                  final double newY2 = matrix.getD() * endX + matrix.getE() * endY + matrix.getF();
                  startX = newX1;
                  startY = newY1;
                  endX = newX2;
                  endY = newY2;
              }
                  break;

              case USE_SUPPORTED :  // is default - fall through intended

              default: {
                  TransformationOperations to1 = TransformationOperations.getFromSvg(matrix);
                  if (to1.hasSkew() || to1.hasScale()) {
                      logger.warn("GradientTransform includes scale or skew - "
                                + "using rotation and translation part only! "
                                + "Rendering might be inaccurate.");
                      logger.debug(to1);

                      Point2D trans = to1.getTranslation();
                      double rot = to1.getRotation();
                      double matA = Math.cos(rot);
                      double matC = -Math.sin(rot);
                      double matE = trans.getX();
                      double matB = Math.sin(rot);
                      double matD = matA;
                      double matF = trans.getY();

                      final double newX1 = matA * startX + matB * startY + matC;
                      final double newY1 = matD * startX + matE * startY + matF;
                      final double newX2 = matA * endX + matB * endY + matC;
                      final double newY2 = matD * endX + matE * endY + matF;
                      startX = newX1;
                      startY = newY1;
                      endX = newX2;
                      endY = newY2;
                  } else { // USE_AS_IS
                      final double newX1 = matrix.getA() * startX + matrix.getB() * startY
                                     + matrix.getC();
                      final double newY1 = matrix.getD() * startX + matrix.getE() * startY
                                     + matrix.getF();
                      final double newX2 = matrix.getA() * endX + matrix.getB() * endY 
                                     + matrix.getC();
                      final double newY2 = matrix.getD() * endX + matrix.getE() * endY 
                                     + matrix.getF();
                      startX = newX1;
                      startY = newY1;
                      endX = newX2;
                      endY = newY2;
                  }
              }
                  break;

            }
        }

        return new LinearGradient(startX, startY, endX, endY, 
                                  false, CycleMethod.NO_CYCLE,
                                  stops);
    }
    

    /**
     * Creates a JavaFX RadialGradient from an SVG radialGradient element.
     * Currently the SVG gradient transformation is ignored by this method.
     * A warning is logged if the SVG gradient element contains a gradientTransform
     * attribute. 
     *
     * @param element The SVG gradient element.
     * @param stops The stops which have already been extracted from the SVG gradient child nodes.
     *
     * @return A JavaFX RadialGradient based on the SVG linear gradient element.
     */
    public RadialGradient createRadialGradient(SVGOMRadialGradientElement element, 
                                               List<Stop> stops) {        
        float centerX = element.getCx().getBaseVal().getValue();
        float centerY = element.getCy().getBaseVal().getValue();
        float radius = element.getR().getBaseVal().getValue();
        float fx = element.getFx().getBaseVal().getValue();
        float fy = element.getFy().getBaseVal().getValue();

        // SVG defines a focus point in absolute coordinates, while 
        // JavaFX uses Polar coordinates
        float dx = fx - centerX;
        float dy = fy - centerY;
        float focusDistance = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        float focusAngle = (float) Math.atan2(dx,  dy);

        SVGMatrix matrix = getGradientTransform(element);
        if (matrix != null) {
            logger.error("GradientTransform for RadialGradient not yet implemented!");
        }

        return new RadialGradient(focusAngle, focusDistance, centerX, centerY, radius, 
                                  false, CycleMethod.NO_CYCLE, 
                                  stops);
    }


    /**
     * @param element The SVG gradient element for which to return its transformation matrix.
     *
     * @return The SVGMatrix interface which can be used to access a gradients
     *         transformation matrix or <code>null</code> if the SVG gradient 
     *         element does not contain a gradientTransform attribute.
     */
    private SVGMatrix getGradientTransform(SVGOMGradientElement element) {
        SVGMatrix result = null;

        // TODO: this should be part of Apache batik
        // However, SVGOMGradientElement.getGradientTransform() is not yet implemented.
        if (!element.getAttribute("gradientTransform").isEmpty()) {
            Attr transform = element.getAttributeNode("gradientTransform");
            SVGOMAnimatedTransformList transformList =
                        new SVGOMAnimatedTransformList(element, transform.getNamespaceURI(), 
                                                       transform.getName(), "");

            SVGTransformList svgTransformations = transformList.getBaseVal();
            
            if (svgTransformations.getNumberOfItems() > 1) {
                throw new RuntimeException("More than one transformation matrix not yet supported");
            }
            if (svgTransformations.getNumberOfItems() == 1) {
                SVGTransform svgTrans = svgTransformations.getItem(0);
                result = svgTrans.getMatrix();
            }
        }

        return result;
    }
}
