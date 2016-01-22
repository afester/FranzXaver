package afester.javafx.svg;

import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

import org.apache.batik.anim.dom.SVGOMAnimatedTransformList;
import org.apache.batik.anim.dom.SVGOMGradientElement;
import org.apache.batik.anim.dom.SVGOMLinearGradientElement;
import org.apache.batik.anim.dom.SVGOMRadialGradientElement;
import org.w3c.dom.Attr;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGTransform;
import org.w3c.dom.svg.SVGTransformList;

public class GradientFactory {

    private GradientPolicy gradientTransformPolicy = GradientPolicy.USE_SUPPORTED;
    
    public GradientFactory() {
    }

    public void setTransformationPolicy(GradientPolicy policy) {
        this.gradientTransformPolicy = policy;
    }
    
    public LinearGradient createLinearGradient(SVGOMLinearGradientElement element, List<Stop> stops) {
        double startX = element.getX1().getBaseVal().getValue();
        double startY = element.getY1().getBaseVal().getValue();
        double endX = element.getX2().getBaseVal().getValue();
        double endY = element.getY2().getBaseVal().getValue();

/************************************************
    ATTENTION:
    A gradientTransform in SVG does not transform the gradient parameter coordinates.
    Instead, the gradient is generated and then the generated gradient is transformed.
    These are two different things!
    In case of a LinearGradient, assumed a gradient definition line with an angle of 45°,
    if the line coordinates are scaled horizontally, this would change the angle.
    If the generated gradient image is scaled horizontally, this would not change the angle
    (or at least in a different matter). Also, since the gradient flows in a 90°
    angle regarding the gradient vector, a skew transformation would never be possible
    by just transforming start and end coordinates of the gradient.
/************************************************/

        SVGMatrix m = getGradientTransform(element);
        if (m != null) {
            switch(gradientTransformPolicy) {
                case DISCARD : 
                    System.err.println("[WARNING] Discarding gradientTransform");
                    break;

                case USE_AS_IS : {
                        TransOperations to1 = TransOperations.getFromSVG(m);
                        if (to1.hasSkew() || to1.hasScale()) {
                            System.err.println("[WARNING] GradientTransform includes scale or skew - this is not yet supported! Rendering might be incorrect.");
                            System.err.println("         " + to1);
                        }
                        double newX1 = m.getA() * startX + m.getB() * startY + m.getC();
                        double newY1 = m.getD() * startX + m.getE() * startY + m.getF();
                        double newX2 = m.getA() * endX + m.getB() * endY + m.getC();
                        double newY2 = m.getD() * endX + m.getE() * endY + m.getF();
                        startX = newX1;
                        startY = newY1;
                        endX = newX2;
                        endY = newY2;
                    }
                    break;

                case USE_SUPPORTED :{
                        TransOperations to1 = TransOperations.getFromSVG(m);
                        if (to1.hasSkew() || to1.hasScale()) {
                            System.err.println("[WARNING] GradientTransform includes scale or skew - using rotation and translation part only! Rendering might be incorrect.");
                            System.err.println("          " + to1);
                            
                            Point2D trans = to1.getTranslation();
                            double rot = to1.getRotation();
                            double a = Math.cos(rot);   double c = -Math.sin(rot);   double e = trans.getX();
                            double b = Math.sin(rot);   double d = a;                double f = trans.getY();

                            double newX1 = a * startX + b * startY + c;
                            double newY1 = d * startX + e * startY + f;
                            double newX2 = a * endX + b * endY + c;
                            double newY2 = d * endX + e * endY + f;
                            startX = newX1;
                            startY = newY1;
                            endX = newX2;
                            endY = newY2;
                        } else { // USE_AS_IS
                            double newX1 = m.getA() * startX + m.getB() * startY + m.getC();
                            double newY1 = m.getD() * startX + m.getE() * startY + m.getF();
                            double newX2 = m.getA() * endX + m.getB() * endY + m.getC();
                            double newY2 = m.getD() * endX + m.getE() * endY + m.getF();
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

    

    public RadialGradient createRadialGradient(SVGOMRadialGradientElement element, List<Stop> stops) {        
        float centerX = element.getCx().getBaseVal().getValue();
        float centerY = element.getCy().getBaseVal().getValue();
        float radius = element.getR().getBaseVal().getValue();
        float fx = element.getFx().getBaseVal().getValue();
        float fy = element.getFy().getBaseVal().getValue();

        // SVG defines a focus point in absolute coordinates, while 
        // JavaFX uses Polar coordinates
        float dx = fx - centerX;
        float dy = fy - centerY;
        float d = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        float alpha = (float) Math.atan2(dx,  dy);
//        System.err.printf("RESULT: %s %s\n", d, alpha);

        SVGMatrix m = getGradientTransform(element);
        if (m != null) {
            System.err.println("[WARNING] GradientTransform for RadialGradient not yet implemented!");
        }

        return new RadialGradient(alpha, d, centerX, centerY, radius, 
                                  false, CycleMethod.NO_CYCLE, 
                                  stops);
    }
    

    private SVGMatrix getGradientTransform(SVGOMGradientElement e) {
        SVGMatrix result = null;

        // e.getGradientTransform() => NYI
        if (!e.getAttribute("gradientTransform").isEmpty()) {
            // TODO: this should be part of ...
            Attr transform = e.getAttributeNode("gradientTransform");
            SVGOMAnimatedTransformList v =
                        new SVGOMAnimatedTransformList(e, transform.getNamespaceURI(), transform.getName(), "");

            SVGTransformList svgTransformations = v.getBaseVal();
            
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
