package afester.javafx.svg;

import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;

import org.apache.batik.anim.dom.SVGOMSVGElement;
import org.apache.batik.anim.dom.SVGStylableElement;
import org.apache.batik.css.dom.CSSOMComputedStyle;
import org.apache.batik.css.dom.CSSOMSVGComputedStyle;
import org.apache.batik.css.dom.CSSOMComputedStyle.ComputedCSSValue;
import org.apache.batik.css.dom.CSSOMSVGComputedStyle.ComputedCSSPaintValue;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGPaint;
import org.w3c.dom.svg.SVGTransform;
import org.w3c.dom.svg.SVGTransformList;
import org.w3c.dom.svg.SVGTransformable;

public class SVGStyleTools {

    private SVGOMSVGElement svgElement = null;

    SVGStyleTools(SVGOMSVGElement svgElement) {
        this.svgElement = svgElement;
    }

    Affine getTransform(SVGTransformable element) {
        Affine fxTrans = null;

        SVGTransformList svgTransformations = element.getTransform().getBaseVal();
        if (svgTransformations.getNumberOfItems() > 1) {
            throw new RuntimeException("More than one transformation matrix not yet supported");
        }
        if (svgTransformations.getNumberOfItems() == 1) {
            SVGTransform svgTrans = svgTransformations.getItem(0);
            SVGMatrix m = svgTrans.getMatrix();

            // SVG: matrix(0.67018323,-0.74219568,0.74219568,0.67018323,0,0)
            //         [   a    c    e  ]
            //         [   b    d    f  ]
            //         [   0    0    1  ]

            // JavaFX: [  mxx  mxy  mxz  tx  ]
            //         [  myx  myy  myz  ty  ]
            //         [  mzx  mzy  mzz  tz  ]

            fxTrans = new Affine(m.getA(), m.getC(), m.getE(), m.getB(), m.getD(), m.getF());
        }

        return fxTrans;
    }

    private Color getFillColor(SVGStylableElement obj) {
        Color result = null;
        
        // svgElement.getComputedStyle() takes care of all styling aspects,
        // like inheritance of style attributes or presentation versus CSS styles
        CSSStyleDeclaration style = svgElement.getComputedStyle(obj, null);
        CSSOMSVGComputedStyle.ComputedCSSPaintValue val = (ComputedCSSPaintValue) style.getPropertyCSSValue("fill");

        if (val.getPaintType() == SVGPaint.SVG_PAINTTYPE_NONE) {    // fill=none
            return null;
        }

        float red = val.getRed().getFloatValue(CSSPrimitiveValue.CSS_NUMBER) / 255;
        float green = val.getGreen().getFloatValue(CSSPrimitiveValue.CSS_NUMBER) / 255;
        float blue = val.getBlue().getFloatValue(CSSPrimitiveValue.CSS_NUMBER) / 255;

        CSSOMComputedStyle.ComputedCSSValue  opacity = (ComputedCSSValue) style.getPropertyCSSValue("fill-opacity");
        float alpha = opacity.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
        result = new Color(red, green, blue, alpha);

        return result;
    }

    
    private Color getStrokeColor(SVGStylableElement obj) {

        Color result = null;

        CSSStyleDeclaration style = svgElement.getComputedStyle(obj, null);
        CSSOMSVGComputedStyle.ComputedCSSPaintValue val = (ComputedCSSPaintValue) style.getPropertyCSSValue("stroke");

        if (val.getPaintType() == SVGPaint.SVG_PAINTTYPE_NONE) {
            return null;    // stroke=none
        }

        float red = val.getRed().getFloatValue(CSSPrimitiveValue.CSS_NUMBER) / 255;
        float green = val.getGreen().getFloatValue(CSSPrimitiveValue.CSS_NUMBER) / 255;
        float blue = val.getBlue().getFloatValue(CSSPrimitiveValue.CSS_NUMBER) / 255;

        CSSOMComputedStyle.ComputedCSSValue opacity = (ComputedCSSValue) style.getPropertyCSSValue("stroke-opacity");
        float alpha = opacity.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);

        result = new Color(red, green, blue, alpha);

        return result;
    }


    void applyStyle(Shape fxObj, SVGStylableElement obj) {

        Color fillColor = getFillColor(obj);
        fxObj.setFill(fillColor);

        Color strokeColor = getStrokeColor(obj);
        fxObj.setStroke(strokeColor);

        CSSStyleDeclaration style = svgElement.getComputedStyle(obj, null);
        CSSOMSVGComputedStyle.ComputedCSSValue swidth = (ComputedCSSValue) style.getPropertyCSSValue("stroke-width");
        if (swidth != null) {
            float strokeWidth = 0;
            if (swidth.getPrimitiveType() == CSSPrimitiveValue.CSS_NUMBER) {
                strokeWidth = swidth.getFloatValue (CSSPrimitiveValue.CSS_NUMBER);
            } else {
                strokeWidth = swidth.getFloatValue (CSSPrimitiveValue.CSS_PX);
            }

            fxObj.setStrokeWidth(strokeWidth);
        }
    }

    
    void applyTextStyle(Text fxObj, SVGStylableElement obj) {
        CSSStyleDeclaration style = svgElement.getComputedStyle(obj, null); // obj.getStyle();

        String fontFamily = null;
        CSSOMComputedStyle.ComputedCSSValue val = (ComputedCSSValue) style.getPropertyCSSValue("font-family");
        if (val != null) {
            fontFamily = val.getCssText();
        }

        float fontSize = 0;
        CSSOMComputedStyle.ComputedCSSValue val2 = (ComputedCSSValue) style.getPropertyCSSValue("font-size");
        if (val2 != null) {
            if (val2.getPrimitiveType() == CSSPrimitiveValue.CSS_NUMBER) {
                fontSize = val2.getFloatValue (CSSPrimitiveValue.CSS_NUMBER);   // https://bugs.launchpad.net/inkscape/+bug/168164
            } else {
                fontSize = val2.getFloatValue (CSSPrimitiveValue.CSS_PX);       // https://bugs.launchpad.net/inkscape/+bug/168164
            }
        }

        System.err.println("FONT: " + fontFamily + "/" + fontSize);
        Font font = Font.font(fontFamily, fontSize);
        fxObj.setFont(font);
        
        applyStyle(fxObj, obj);
        
/*        font-style:normal;
        font-variant:normal;
        font-weight:normal;
        font-stretch:normal;
*/
    }

}
