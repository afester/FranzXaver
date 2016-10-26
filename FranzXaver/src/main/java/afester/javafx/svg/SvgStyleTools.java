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

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;

import org.apache.batik.anim.dom.SVGOMSVGElement;
import org.apache.batik.anim.dom.SVGStylableElement;
import org.apache.batik.css.dom.CSSOMComputedStyle;
import org.apache.batik.css.dom.CSSOMComputedStyle.ComputedCSSValue;
import org.apache.batik.css.dom.CSSOMSVGComputedStyle;
import org.apache.batik.css.dom.CSSOMSVGComputedStyle.ComputedCSSPaintValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGPaint;
import org.w3c.dom.svg.SVGTransform;
import org.w3c.dom.svg.SVGTransformList;
import org.w3c.dom.svg.SVGTransformable;

import java.util.HashMap;
import java.util.Map;

public class SvgStyleTools {
    private static final Logger logger = LogManager.getLogger();

    protected SVGOMSVGElement svgElement = null;
    private Map<String, Paint> paints = new HashMap<>();

    SvgStyleTools(SVGOMSVGElement svgElement) {
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
            SVGMatrix matrix = svgTrans.getMatrix();

            // SVG: matrix(0.67018323,-0.74219568,0.74219568,0.67018323,0,0)
            //         [   a    c    e  ]
            //         [   b    d    f  ]
            //         [   0    0    1  ]

            // JavaFX: [  mxx  mxy  mxz  tx  ]
            //         [  myx  myy  myz  ty  ]
            //         [  mzx  mzy  mzz  tz  ]

            fxTrans = new Affine(matrix.getA(), matrix.getC(), matrix.getE(), 
                                 matrix.getB(), matrix.getD(), matrix.getF());
        }

        return fxTrans;
    }

    private Paint getFillColor(SVGStylableElement obj) {
        Paint result = null;

        // svgElement.getComputedStyle() takes care of all styling aspects,
        // like inheritance of style attributes or presentation versus CSS styles
        CSSStyleDeclaration style = svgElement.getComputedStyle(obj, null);
        CSSOMSVGComputedStyle.ComputedCSSPaintValue val = 
                (ComputedCSSPaintValue) style.getPropertyCSSValue("fill");

        if (val.getPaintType() == SVGPaint.SVG_PAINTTYPE_NONE) {    // fill=none
            return null;
        }

        if (val.getPaintType() == SVGPaint.SVG_PAINTTYPE_URI) {
            String uri = val.getUri();
            if (uri.startsWith("file:#")) {
                uri = uri.substring("file:#".length());
            }
            result = paints.get(uri);
        }

        if (val.getPaintType() == SVGPaint.SVG_PAINTTYPE_RGBCOLOR) {
            float red = val.getRed().getFloatValue(CSSPrimitiveValue.CSS_NUMBER) / 255;
            float green = val.getGreen().getFloatValue(CSSPrimitiveValue.CSS_NUMBER) / 255;
            float blue = val.getBlue().getFloatValue(CSSPrimitiveValue.CSS_NUMBER) / 255;
    
            CSSOMComputedStyle.ComputedCSSValue  opacity = 
                    (ComputedCSSValue) style.getPropertyCSSValue("fill-opacity");
            float alpha = opacity.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
            result = new Color(red, green, blue, alpha);
        }

        return result;
    }


    private Paint getStrokeColor(SVGStylableElement obj) {

        Paint result = null;

        CSSStyleDeclaration style = svgElement.getComputedStyle(obj, null);
        CSSOMSVGComputedStyle.ComputedCSSPaintValue val = 
                    (ComputedCSSPaintValue) style.getPropertyCSSValue("stroke");

        if (val.getPaintType() == SVGPaint.SVG_PAINTTYPE_NONE) {
            return null;    // stroke=none
        }

        if (val.getPaintType() == SVGPaint.SVG_PAINTTYPE_URI) {
            String uri = val.getUri();
            if (uri.startsWith("file:#")) {
                uri = uri.substring("file:#".length());
            }
            result = paints.get(uri);
        }

        if (val.getPaintType() == SVGPaint.SVG_PAINTTYPE_RGBCOLOR) {
            float red = val.getRed().getFloatValue(CSSPrimitiveValue.CSS_NUMBER) / 255;
            float green = val.getGreen().getFloatValue(CSSPrimitiveValue.CSS_NUMBER) / 255;
            float blue = val.getBlue().getFloatValue(CSSPrimitiveValue.CSS_NUMBER) / 255;
    
            CSSOMComputedStyle.ComputedCSSValue opacity = 
                    (ComputedCSSValue) style.getPropertyCSSValue("stroke-opacity");
            float alpha = opacity.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
    
            result = new Color(red, green, blue, alpha);
        }
        
        return result;
    }


    /**
     * Applies the styling of an svg element to a JavaFX Shape object.
     *
     * @param fxObj   The JavaFX Shape object to style.
     * @param element The SVG DOM element which defines the styling.
     */
    void applyStyle(Shape fxObj, SVGStylableElement element) {

        // fill
        Paint fillColor = getFillColor(element);
        fxObj.setFill(fillColor);

        // stroke
        Paint strokeColor = getStrokeColor(element);
        fxObj.setStroke(strokeColor);

        // stroke-width
        CSSStyleDeclaration style = svgElement.getComputedStyle(element, null);
        CSSOMSVGComputedStyle.ComputedCSSValue swidth = 
                    (ComputedCSSValue) style.getPropertyCSSValue("stroke-width");
        if (swidth != null) {
            float strokeWidth = 0;
            if (swidth.getPrimitiveType() == CSSPrimitiveValue.CSS_NUMBER) {
                strokeWidth = swidth.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
            } else {
                strokeWidth = swidth.getFloatValue(CSSPrimitiveValue.CSS_PX);
            }

            fxObj.setStrokeWidth(strokeWidth);
        }

        // stroke-dasharray
        CSSOMSVGComputedStyle.ComputedCSSValue strokeDashArray = 
                (ComputedCSSValue) style.getPropertyCSSValue("stroke-dasharray");
        if (strokeDashArray != null
            && strokeDashArray.getCssValueType() == CSSValue.CSS_VALUE_LIST) {
            for (int i = 0;  i < strokeDashArray.getLength();  i++) {
                double dashLength = strokeDashArray.getValue().item(i).getFloatValue();
                fxObj.getStrokeDashArray().add(dashLength);            
            }
        }

        // stroke-dashoffset
        CSSOMSVGComputedStyle.ComputedCSSValue strokeDashOffset = 
                (ComputedCSSValue) style.getPropertyCSSValue("stroke-dashoffset");
        if (strokeDashOffset != null) {
            double dashOffset = strokeDashOffset.getValue().getFloatValue();
            fxObj.setStrokeDashOffset(dashOffset);
        }
    }


    void applyTextStyle(Text fxObj, SVGStylableElement obj) {
        CSSStyleDeclaration style = svgElement.getComputedStyle(obj, null); // obj.getStyle();

        String fontFamily = null;
        CSSOMComputedStyle.ComputedCSSValue val = 
                    (ComputedCSSValue) style.getPropertyCSSValue("font-family");
        if (val != null) {
            fontFamily = val.getCssText();
        }

        float fontSize = 0;
        CSSOMComputedStyle.ComputedCSSValue val2 = 
                    (ComputedCSSValue) style.getPropertyCSSValue("font-size");
        if (val2 != null) {
            if (val2.getPrimitiveType() == CSSPrimitiveValue.CSS_NUMBER) {
                fontSize = val2.getFloatValue(CSSPrimitiveValue.CSS_NUMBER); // https://bugs.launchpad.net/inkscape/+bug/168164
            } else {
                fontSize = val2.getFloatValue(CSSPrimitiveValue.CSS_PX);     // https://bugs.launchpad.net/inkscape/+bug/168164
            }
        }

        logger.debug("Font: {}/{}", fontFamily, fontSize);
        Font font = Font.font(fontFamily, fontSize);
        fxObj.setFont(font);

        applyStyle(fxObj, obj);
        
/*        font-style:normal;
        font-variant:normal;
        font-weight:normal;
        font-stretch:normal;
*/
    }

    public void addPaint(String id, Paint paintObject) {
        paints.put(id,  paintObject);
    }

    public Paint getPaint(String href) {
        return paints.get(href);
    }

}
