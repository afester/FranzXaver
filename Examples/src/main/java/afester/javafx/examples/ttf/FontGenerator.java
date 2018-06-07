/*
 * Copyright 2017 Andreas Fester
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

package afester.javafx.examples.ttf;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;

import afester.javafx.examples.Example;
import afester.javafx.examples.image.ArrayDump;
import afester.javafx.examples.image.ImageConverter;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.Stage;


/**
 * Example for loading a custom true type font
 */
@Example(desc = "Custom True Type Font",
         cat  = "Basic JavaFX")
public class FontGenerator extends Application {
 
    public static void main(String[] args) {
        // The following properties are required on Linux for improved text rendering
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    private Font f;
    private TextField inputLine;
    private Group glyphs;
    private List<GlyphData> glyphData = new ArrayList<>();
    private CheckBox showGlyphBounds;
    private HBox snapshots;
    
    private class Digit extends Group {

        public Digit(int value) {
            Text background = new Text("8.");
            background.setTextOrigin(VPos.TOP);
            background.setFont(f);
            background.setFill(new Color(0.15, 0.15, 0.15, 1.0));

//            Bounds b = background.getLayoutBounds();
//            System.err.printf("%s/%s\n", b.getWidth(), b.getHeight());
//            Rectangle r = new Rectangle(b.getWidth() + 6, b.getHeight());
//            r.setFill(Color.YELLOW);

            Text sampleText = new Text(Integer.toString(value));
            sampleText.setTextOrigin(VPos.TOP);
            sampleText.setFont(f);
            sampleText.setFill(Color.RED);

            getChildren().addAll(/*r,*/ background, sampleText);
        }
    }
    
    
    @Override
    public void start(Stage stage) {
        stage.setTitle("Font Generator");

        Text t = new Text();

        InputStream is = getClass().getResourceAsStream("DSEG7Classic-BoldItalic.ttf");
        f = Font.loadFont(is, 72);
        // f = Font.font("Arial", FontWeight.EXTRA_BOLD, FontPosture.ITALIC, 12.0);

        FontSelectionPanel fsp = new FontSelectionPanel(f);
        fsp.setOnFontChanged( e-> {
            f = Font.font(e.getFamily(), e.getWeight(), e.isItalic() ? FontPosture.ITALIC : FontPosture.REGULAR, e.getSize());
            t.setFont(f);
            updateGlyphs();
        });

        inputLine = new TextField("0123456789,mAVT°C");
        inputLine.textProperty().addListener((obj, oldVal, newVal) -> {
            t.setText(newVal);
            updateGlyphs();
        });

        t.setText(inputLine.getText());
        t.setFont(f);

//
//        Color[] colors = {Color.RED, Color.VIOLET, Color.BLUE, Color.BLACK, Color.GREEN};
//
//        Group g = new Group();
//        g.setAutoSizeChildren(false);
//        for (int i = 0; i < 5; i++) {
//            Rectangle r2 = new Rectangle();
//            r2.setY(i * 20);
//            r2.setX(40);
//            r2.setWidth(100);
//            r2.setHeight(10);
//            r2.setFill(colors[i]);
//            g.getChildren().add(r2);
//        }
//        disp.getChildren().add(g);
        
//        for (int x = 62;  x < 680;  x += 59) {
//            Line l = new Line(x, 0, x, 76);
//            l.setStroke(Color.WHITE);
//            disp.getChildren().addAll(l);
//        }

//        Button b = new Button("Export");
//        b.setOnAction(e -> {
//            SnapshotParameters params = new SnapshotParameters();
//            Image result = disp.snapshot(params, null);
//
//            ImageConverter ic = new ImageConverter();
//            byte[] rgb565 = ic.getRGB565asByte(result);
//            ArrayDump ad = new ArrayDump(rgb565);
//            ad.dumpAll16(System.err, (int) result.getWidth());
//
//            System.err.println();
//
//            byte[] compressed = compressRLE(rgb565);
//            System.err.println("Compressed size:" + compressed.length);
//            ArrayDump cd = new ArrayDump(compressed);
//            cd.dumpAll(System.err);
//
//            // ad.dumpAll2(System.err);
//        });

//        Button saveBtn = new Button("Save as ...");
//        saveBtn.setOnAction(e -> {
//            FileChooser fileChooser = new FileChooser();
//            fileChooser.setTitle("Save Image file ...");
//            File theFile = fileChooser.showSaveDialog(stage);
//            if (theFile != null) {
//
//                SnapshotParameters params = new SnapshotParameters();
//                WritableImage wim = disp.snapshot(params, null);
//                PixelFormat<?> pif = wim.getPixelReader().getPixelFormat();
//                System.err.println("IMAGE TYPE: " + pif.getType());
//
//                PixelFormat pf = PixelFormat.createByteIndexedInstance(new int[] {5, 10, 15});
//
//                WritableImage img = new WritableImage(100, 100);
//                System.err.println("NEW IMAGE TYPE: " + img.getPixelReader().getPixelFormat().getType());
//                try {
//                    ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", theFile);
//
//                    BufferedImage bi = SwingFXUtils.fromFXImage(wim, null);
//                    System.err.println("IMAGE TYPE: " + getImageType(bi.getType()));
//
//                    String[] available = ImageIO.getWriterFileSuffixes();
//                    for (String a : available) {
//                        System.err.println("  " + a);
//                    }
//                    Iterator<ImageWriter> wrs = ImageIO.getImageWritersByFormatName("png");
//                    wrs.forEachRemaining(iw -> {
//                        ImageWriteParam iwp;
//                        System.err.println(iw);
//                    });
//
//                    ByteArrayOutputStream result = new ByteArrayOutputStream();
//                    ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", result);
//
//                    HexDump hd = new HexDump(result.toByteArray());
//                    hd.dumpAll(System.err);
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }

//                try {
//                    FileInputStream input = new FileInputStream(theFile.getAbsolutePath());
//                    imageView.setImage(new Image(input));
//                    primaryStage.sizeToScene();
//                } catch (FileNotFoundException e1) {
//                    e1.printStackTrace();
//                }
//            }

//            ImageConverter ic = new ImageConverter();
//            byte[] rgb565 = ic.getRGB565(result);
//            ArrayDump ad = new ArrayDump(rgb565);
//            ad.dumpAll16(System.err, (int) result.getWidth());
//
//            System.err.println();
//
//            byte[] compressed = compressRLE(rgb565);
//            System.err.println("Compressed size:" + compressed.length);
//            ArrayDump cd = new ArrayDump(compressed);
//            cd.dumpAll(System.err);
//
//            // ad.dumpAll2(System.err);
//        });

        
//        ArrayList<Digit> digits = new ArrayList<Digit>();
//        Group disp = new Group();
//        Rectangle r = new Rectangle(700, 76);
//        disp.getChildren().add(r);
//        for (int value = 0;  value < 10;  value++) {
//            Digit d = new Digit(value);
//            digits.add(d);
//            d.setLayoutY(2);
//            d.setLayoutX(64 * value);
//            
//            Line l = new Line((value+1)*64, 0, (value+1)*64, 76);
//            l.setStroke(Color.WHITE);
//
//            disp.getChildren().addAll(d, l);
//        }
//
//        HBox snapshots = new HBox();
//        snapshots.setBackground(new Background(new BackgroundFill(Color.AQUAMARINE, new CornerRadii(0), new Insets(0, 0, 0,0))));
//        snapshots.setSpacing(5);
//        Button snapshotBtn = new Button("Snapshot");
//        snapshotBtn.setOnAction(e -> {
//            SnapshotParameters params = new SnapshotParameters();
//
//            snapshots.getChildren().clear();
//            digits.forEach(c -> {
//                Image result = c.snapshot(params, null);
//                snapshots.getChildren().add(new ImageView(result));
//                stage.sizeToScene();
//            });
//        });

        HBox bottomBox = new HBox();
        bottomBox.setSpacing(10);
        showGlyphBounds = new CheckBox("Show glyph bounds");
        showGlyphBounds.setOnAction(e -> {
            updateGlyphs();
        });

        snapshots = new HBox();
        
        
        Button exportButton = new Button("Export ...");
        exportButton.setOnAction(e -> {

            try {
                FileOutputStream fos = new FileOutputStream("font.c");
                PrintStream out = new PrintStream(fos);
    
                SnapshotParameters params = new SnapshotParameters();
                snapshots.getChildren().clear();
                int glyphIdx = 0;
                glyphData.sort( (a, b) -> { return a.character > b.character ? 1 : -1; } );
                Map<Integer, String> charSetMap = new HashMap<>();
                for (GlyphData gd : glyphData) {
                    Image img = gd.glyphNode.snapshot(params, null);
                    snapshots.getChildren().add(new ImageView(img));
                    stage.sizeToScene();
                    
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", baos);
    
                    ImageConverter ic = new ImageConverter();
                    byte[] rgb565 = ic.getRGB565asByte(img);

                    byte[] compressed = compressRLE(rgb565);
                    System.err.println("Compressed size:" + compressed.length);

                    out.println(String.format("// Glyph data: '%c'", gd.character));
                    String glyphId = String.format("g%03d", glyphIdx++);
                    charSetMap.put((int) gd.character, glyphId);
                    out.println(String.format("uint8_t %s = ", glyphId));
                    ArrayDump cd = new ArrayDump(compressed);
                    cd.dumpAll(out);
                    out.println(";\n");
                }

                out.print("uint8_t* charSet[224] = {");
                for (int idx = ' ';  idx < 256;  idx++) {
                    String glyphId = charSetMap.get(idx);
                    if (glyphId == null) {
                        glyphId = "NULL";
                    }

                    if (idx > 0) {
                        out.print(", ");
                    }
                    if ((idx % 8) == 0) {
                        out.println();
                    }
                    out.print(" " + glyphId);
                    out.print(String.format(" /*%03d'%c'*/", idx, (char) idx));
                }
                out.println("};");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        bottomBox.getChildren().addAll(exportButton, showGlyphBounds);

        glyphs = new Group();
        updateGlyphs();

        VBox box = new VBox();
        box.setSpacing(10);
        box.getChildren().addAll(fsp, inputLine, t, glyphs, bottomBox, snapshots); // , b, saveBtn, disp, snapshotBtn, snapshots);// , colorPicker);

        Scene scene  = new Scene(box);

        stage.setScene(scene);
        stage.show();
    }


    public Bounds reportSize(String s, Font myFont) {
        Text text = new Text(s);
        text.setFont(myFont);
        Bounds tb = text.getBoundsInLocal();
        Rectangle stencil = new Rectangle(
                tb.getMinX(), tb.getMinY(), tb.getWidth(), tb.getHeight()
        );

        Shape intersection = Shape.intersect(text, stencil);

        Bounds ib = intersection.getBoundsInLocal();
        return ib;
//        System.out.println(
//                "Text size: " + ib.getWidth() + ", " + ib.getHeight()
//        );
    }
    
    
    private class GlyphData {
        char character;         // the character
        float charWidth;        // the width of the character
        Bounds charBox;         // the bounding box of the visible part of the glyph (might be much smaller than charWidth X charHeight)
        public Group glyphNode; // the javafx node which renders the glyph

        @Override
        public String toString() {
            return "" + character;
        }
    }

    // private float xpos = 0;
    private double yMin = 0;
    private double yMax = 0;
    private void updateGlyphs() {


// Calculate all glyphs
        glyphData.clear();
//xpos = 0;
        yMin = 0;
        yMax = 0;
        final FontMetrics fm = Toolkit.getToolkit().getFontLoader().getFontMetrics(f);
        // final float charHeight = fm.getXheight();    // height of non-capital "x" letter
        ///final float charHeight = fm.getLineHeight();    // line height - might be much larger than the glyph sizes
        inputLine.getText().chars().forEach(e -> {
            GlyphData gd = new GlyphData();
            gd.charWidth = fm.getCharWidth((char) e);  // width of the given character
            gd.charBox = reportSize(String.valueOf((char) e), f);
            gd.character =  (char) e;

            if (gd.charBox.getMinY() < yMin) {
                yMin = gd.charBox.getMinY();
            }
            if (gd.charBox.getMaxY() > yMax) {
                yMax = gd.charBox.getMaxY();
            }

            glyphData.add(gd);
        });


        glyphs.getChildren().clear();

        final double charHeight = yMax - yMin;
        float xpos = 0;
        for (GlyphData gd : glyphData) {

            Group gg = new Group();

            // background shape
            final Rectangle r = new Rectangle(xpos,  yMin, gd.charWidth, charHeight);
            r.setFill(Color.BLACK);
//            r.setFill(null);
//            r.setStroke(Color.BLACK);
//            r.getStrokeDashArray().add(0, 5.0);
//            r.getStrokeDashArray().add(1, 5.0);
            gg.getChildren().add(r);

        // Bounding box of the character
            if (showGlyphBounds.isSelected()) {
                final Rectangle gr = new Rectangle(xpos + gd.charBox.getMinX(), gd.charBox.getMinY(),
                                                   gd.charBox.getWidth(), gd.charBox.getHeight());
                //gr.setFill(Color.BLACK);
                //gr.setStroke(null);
                gr.setFill(null);
                gr.setStroke(Color.WHITE);
                gr.getStrokeDashArray().add(0, 5.0);
                gr.getStrokeDashArray().add(1, 5.0);
                gg.getChildren().add(gr);
            }

            // glyph
            Text c = new Text(xpos, 0, String.valueOf(gd.character));
            // c.setBoundsType(TextBoundsType.LOGICAL_VERTICAL_CENTER);
            //c.setTextOrigin(VPos.BOTTOM);  // Default is VPos.BASELINE!!!!
            c.setFont(f);
            c.setFill(Color.RED);
            gg.getChildren().add(c);
            
            gd.glyphNode = gg;
            glyphs.getChildren().add(gg);

            xpos += gd.charWidth; //  + 4;
        }

        //Line l1 = new Line(0, yMin, xpos, yMin);
        //Line l2 = new Line(0, yMax, xpos, yMax);
        //glyphs.getChildren().addAll(l1, l2);
    }

    private String getImageType(int type) {
        String result = "UNKNOWN";
        switch(type) {
            case BufferedImage.TYPE_CUSTOM  : result = "TYPE_CUSTOM"; break;
            case BufferedImage.TYPE_INT_RGB  : result = "TYPE_INT_RGB"; break;
            case BufferedImage.TYPE_INT_ARGB  : result = "TYPE_INT_ARGB"; break;
            case BufferedImage.TYPE_INT_ARGB_PRE  : result = "TYPE_INT_ARGB_PRE"; break;
            case BufferedImage.TYPE_INT_BGR  : result = "TYPE_INT_BGR"; break;
            case BufferedImage.TYPE_3BYTE_BGR  : result = "TYPE_3BYTE_BGR"; break;
            case BufferedImage.TYPE_4BYTE_ABGR  : result = "TYPE_4BYTE_ABGR"; break;
            case BufferedImage.TYPE_4BYTE_ABGR_PRE  : result = "TYPE_4BYTE_ABGR_PRE"; break;
            case BufferedImage.TYPE_USHORT_565_RGB  : result = "TYPE_USHORT_565_RGB"; break;
            case BufferedImage.TYPE_USHORT_555_RGB  : result = "TYPE_USHORT_555_RGB"; break;
            case BufferedImage.TYPE_BYTE_GRAY  : result = "TYPE_BYTE_GRAY"; break;
            case BufferedImage.TYPE_USHORT_GRAY  : result = "TYPE_USHORT_GRAY"; break;
            case BufferedImage.TYPE_BYTE_BINARY  : result = "TYPE_BYTE_BINARY"; break;
            case BufferedImage.TYPE_BYTE_INDEXED  : result = "TYPE_BYTE_INDEXED"; break;
        }
        
        return result;
    }

    private byte[] compressRLEinternal(byte[] data, byte[] dest) {
        int count = 0;
        int oldValue = -1;  // value always <= 65536
        int value = 0;
        int upper = 0;
        int lower = 0;
        int resultLength = 0;

        ArrayList<Integer> allValues = new ArrayList<>();
        for (int idx = 0;  idx < data.length;  ) {
            upper = (short) (data[idx++] & 0xff) << 8;
            lower = (short) (data[idx++] & 0xff);
            value = upper + lower;

            if (!allValues.contains(value)) {
                allValues.add(value);
            }

            count++;

            if (oldValue == -1) {
                oldValue = value;
                count = 0;
            }

            if (value != oldValue) {
                if (dest != null) {
                    dest[resultLength + 0] = (byte) count;                      // TODO: Overflow!
                    dest[resultLength + 1] = (byte) allValues.indexOf(value);   // TODO: Overflow!
                    //dest[resultLength + 1] = (byte) upper;
                    //dest[resultLength + 2] = (byte) lower;
                }
                resultLength += 2; // 3;
                count = 0;
            }
            oldValue = value;
        }
        if (dest != null) {
            dest[resultLength + 0] = (byte) (count+1);                  // TODO: Overflow!
            dest[resultLength + 1] = (byte) allValues.indexOf(value);   // TODO: Overflow!
            //dest[resultLength + 1] = (byte) upper;
            //dest[resultLength + 2] = (byte) lower;
        }
        resultLength += 2; // 3;
        
        if (dest == null) {
            System.err.println(allValues);
            return new byte[resultLength];
        }
        return dest;
    }
    
    private byte[] compressRLE(byte[] data) {
        byte[] buffer = compressRLEinternal(data, null);
        compressRLEinternal(data, buffer);
        return buffer;
    }
}
