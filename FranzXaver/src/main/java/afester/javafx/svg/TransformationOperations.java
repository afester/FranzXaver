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
import javafx.scene.transform.Affine;

import org.w3c.dom.svg.SVGMatrix;

public class TransformationOperations {
    private double rotation = 0;
    private double scaleX = 0;
    private double scaleY = 0;
    private double skewX = 0;
    private double transX = 0;
    private double transY = 0;

    private TransformationOperations() {
    }

    /**
     * Creates a {@link TransformationOperations} object from a JavaFX affine 
     * transformation.
     *
     * @param affine The {@link Affine} object for which to calculate the
     *               transformation operations.
     *
     * @return A {@link TransformationOperations} object which contains the
     *         translation, scale, rotation, and skew of the given transformation matrix. 
     */
    public static TransformationOperations getFromAffine(Affine affine) {
        TransformationOperations result = new TransformationOperations();

        // http://stackoverflow.com/a/32125700/1611055
        // JavaFX: [  mxx  mxy  0  tx  ]
        //         [  myx  myy  0  ty  ]
        //         [    0    0  0   0  ]

        result.rotation = Math.atan2(affine.getMxy(), affine.getMxx());
        double denom = Math.pow(affine.getMxx(), 2) + Math.pow(affine.getMxy(), 2);
        result.scaleX = Math.sqrt(denom);
        result.scaleY = (affine.getMxx() * affine.getMyy() - affine.getMyx() * affine.getMxy()) 
                        / result.scaleX;
        result.skewX = Math.atan2(affine.getMxx() * affine.getMyx()
                                + affine.getMxy() * affine.getMyy(), denom);
        result.transX = affine.getTx();
        result.transY = affine.getTy();

        return result;
    }


    /**
     * Creates a {@link TransformationOperations} object from an SVG transformation
     * matrix.
     *
     * @param matrix The {@link SVGMatrix} object for which to calculate the
     *               transformation operations.
     *
     * @return A {@link TransformationOperations} object which contains the
     *         translation, scale, rotation, and skew of the given transformation matrix. 
     */
    public static TransformationOperations getFromSvg(SVGMatrix matrix) {
        TransformationOperations result = new TransformationOperations();
    
        // http://stackoverflow.com/a/32125700/1611055
    
        // SVG: [   a    c   0   e  ]
        //      [   b    d   0   f  ]
        //      [   0    0   0   0 ]
    
        result.rotation = Math.atan2(matrix.getC(), matrix.getA());
        double denom = Math.pow(matrix.getA(), 2) + Math.pow(matrix.getC(), 2);
        result.scaleX = Math.sqrt(denom);
        result.scaleY = (matrix.getA() * matrix.getD() - matrix.getB() * matrix.getC()) 
                        / result.scaleX;
        result.skewX = Math.atan2(matrix.getA() * matrix.getB() + matrix.getC() *  matrix.getD(),
                                  denom);
        result.transX = matrix.getE();
        result.transY = matrix.getF();

        return result;
    }

    /**
     * @return The translation part of the transformation matrix.
     */
    public Point2D getTranslation() {
        return new Point2D(transX, transY);
    }

    /**
     * @return The skew part (in X- and Y-direction) of the transformation matrix.
     */
    public Point2D getSkew() {
        return new Point2D(skewX, 0);
    }
    
    /**
     * @return The scale part (in x- and y-direction) of the transformation matrix.
     */
    public Point2D getScale() {
        return new Point2D(scaleX, scaleY);
    }

    /**
     * @return The rotation part (in degrees) of the transformation matrix.
     */
    public double getRotation() {
        return rotation;
    }

    /**
     * @return <code>true</code> if the transformation matrix contains a rotation,
     *         <code>false</code> otherwise.
     */
    public boolean hasRotation() {
        if (rotation < Math.ulp(rotation)) {
            return false;
        }

        return true;
    }

    /**
     * @return <code>true</code> if the transformation matrix contains a scaling,
     *         <code>false</code> otherwise.
     */
    public boolean hasScale() {
        if (scaleX < Math.ulp(scaleX)
            && scaleY < Math.ulp(scaleY)) {
            return false;
        }

        return true;
    }

    /**
     * @return <code>true</code> if the transformation matrix contains a skew,
     *         <code>false</code> otherwise.
     */
    public boolean hasSkew() {
        if (skewX < Math.ulp(skewX)) {
            return false;
        }

        return true;
    }

    /**
     * @return <code>true</code> if the transformation matrix contains a translation,
     *         <code>false</code> otherwise.
     */
    public boolean hasTranslation() {
        if (transX < Math.ulp(transX)
            && transY < Math.ulp(transY)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        String rot = "None";
        if (hasRotation()) {
            rot = "" + getRotation();
        }

        String scale = "None";
        if (hasScale()) {
            scale = getScale().toString();
        }

        String trans = "None";
        if (hasTranslation()) {
            trans = getTranslation().toString();
        }

        String skew = "None";
        if (hasSkew()) {
            skew = getSkew().toString();
        }

        return String.format("Rotation=%s, Scale=%s, Skew=%s, Translation=%s",  
                             rot, scale, skew, trans);
    }
}
