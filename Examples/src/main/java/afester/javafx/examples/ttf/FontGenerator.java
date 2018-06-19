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
import java.io.File;
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
import afester.javafx.examples.image.RleEncoder;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
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
    private HBox glyphs;
    private List<GlyphData> glyphData = new ArrayList<>();
    private CheckBox showVisualBounds;
    private CheckBox showLogicalBounds;
    private CheckBox showTextOrigin;
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
        showVisualBounds = new CheckBox("Show visual bounds");
        showVisualBounds.setOnAction(e -> {
            updateGlyphs();
        });

        showLogicalBounds = new CheckBox("Show logical bounds");
        showLogicalBounds.setOnAction(e -> {
            updateGlyphs();
        });

        showTextOrigin = new CheckBox("Show Text Origin");
        showTextOrigin.setOnAction(e -> {
            updateGlyphs();
        });

        snapshots = new HBox();
        
        
        Button exportButton = new Button("Export ...");
        exportButton.setOnAction(e -> {

            try {
                FileOutputStream fos = new FileOutputStream("font.c");
                PrintStream out = new PrintStream(fos);

                out.println("#include <stddef.h>");
                out.println("#include <ILI9481.h>");
                out.println("#include <avr/pgmspace.h>");
                out.println();

                // Create the glyph bitmap data
                SnapshotParameters params = new SnapshotParameters();
                snapshots.getChildren().clear();
                int glyphIdx = 0;
                glyphData.sort( (a, b) -> { return a.character > b.character ? 1 : -1; } );
                Map<Integer, GlyphData> charSetMap = new HashMap<>();
                RleEncoder rle = new RleEncoder();
                for (GlyphData gd : glyphData) {
                	System.err.printf("Node: %s (%s / %s)\n", gd.glyphNode, gd.glyphNode.getWidth(),
                			gd.glyphNode.getHeight());
                    Image img = gd.glyphNode.snapshot(null, null);
                    System.err.printf("%s X %s\n", img.getWidth(), img.getHeight());

                    File theFile = new File(gd.glyphId + ".png");
                    ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", theFile);

                    ImageConverter ic = new ImageConverter();
                    List<Short> palette = new ArrayList<>();
/////////////////////////7
		            short[] rgb565 = ic.getRGB565(img);

		            // convert bitmap to indexed bitmap
		            byte[] bitmap = new byte[rgb565.length];
		            for (int idx = 0;  idx < rgb565.length;  idx++) {
		            	short value = rgb565[idx];
		
		                // lookup value index
		                int colorIdx = palette.indexOf(value);
		                if (colorIdx == -1) {
		                	colorIdx = palette.size();
		                	palette.add(value);
		                }
	
		                bitmap[idx] = (byte) colorIdx;	// TODO: max. 256 colors
		            }
	
		            int newLength = rle.rleEncode_4plus4(bitmap);
		            byte[] bitmapRle = new byte[newLength];
		            System.arraycopy(bitmap, 0, bitmapRle, 0, newLength);
		            
		            ArrayDump ad = new ArrayDump(bitmapRle);
		            int width = (int) img.getWidth();
		            int height = (int) img.getHeight();

		            out.printf("const Bitmap8 %s PROGMEM = {%s, %s,\n", gd.glyphId, width, height);
		            ad.dumpAll(width, out);
		            out.println("};\n");
/////////////////
                    
//                    System.err.printf("%s X %s\n", img.getWidth(), img.getHeight());
//                    snapshots.getChildren().add(new ImageView(img));
//                    stage.sizeToScene();
//                    
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", baos);
//    
//                    byte[] rgb565 = ic.getRGB565asByte(img);
//
//                    
//                    
//		            int newLength = ImageConverter.rleEncode(rgb565);
//		            byte[] compressed= new byte[newLength];
//		            System.arraycopy(rgb565, 0, compressed, 0, newLength);
//
//                    //byte[] compressed = compressRLE(rgb565);
//                    System.err.println("Compressed size:" + compressed.length);
//
//                    out.println(String.format("// Glyph data: '%c'", gd.character));
//                    gd.glyphId = String.format("g%03d", glyphIdx++);
//                    charSetMap.put((int) gd.character, gd);
//                    
//                    //out.println(String.format("uint8_t %s = ", gd.glyphId));
//                    out.println(String.format("static const  Bitmap8 %s PROGMEM = {%s, %s,",
//                    		gd.glyphId, (int) img.getWidth(), (int) img.getHeight()));
//                    		//gd.glyphId, (int) gd.charWidth, (int) gd.charHeight));
//                    ArrayDump cd = new ArrayDump(compressed);
//                    cd.dumpAll(out);
//                    out.println("};\n");
                }

                // create the font array
                //out.println("struct fontData {");
                //out.println("  uint8_t *glyph;");
                //out.println("  uint8_t width");
                //out.println("};");
                //out.print("struct fontData charSet[224] = {");
                out.print("const Bitmap8* charSet[224] = {");
                for (int idx = ' ';  idx < 256;  idx++) {

                    GlyphData gd = charSetMap.get(idx);
                    String glyphId = "NULL";
                    int glyphWidth = 0;
                    if (gd != null) {
                        glyphId = "&" + gd.glyphId;
                        glyphWidth = (int) gd.charWidth;
                    }

                    if (idx > ' ') {
                        out.print(", ");
                    }
                    if ((idx % 8) == 0) {
                        out.println();
                    }

                    //out.print(String.format("{%s /*%03d'%c'*/, %d}", glyphId, idx, (char) idx, glyphWidth));
                    out.print(String.format("%s /*%03d'%c'*/", glyphId, idx, (char) idx));
                }
                out.println("};");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        bottomBox.getChildren().addAll(exportButton, showVisualBounds, showLogicalBounds, showTextOrigin);

        glyphs = new HBox();

        glyphMetricArea = new Pane();
        glyphMetricArea.setStyle("-fx-background-color: lightgray; -fx-border-color: darkgray; -fx-border-style: dashed;");
        updateGlyphs();
        
        VBox box = new VBox();
        box.setSpacing(10);
        box.getChildren().addAll(fsp, inputLine, t, glyphs, bottomBox, snapshots,
                                 glyphMetricArea); // , b, saveBtn, disp, snapshotBtn, snapshots);// , colorPicker);

        Scene scene  = new Scene(box);

        stage.setScene(scene);
        stage.show();
    }

    private Pane glyphMetricArea;

    double xpos = 10;
    private Pane updateGlyphs() {

        glyphMetricArea.getChildren().clear();

        xpos = 10;
        inputLine.getText().chars().forEach(e -> {
                
            Text t = new Text(xpos, 100, String.valueOf((char) e));  // inputLine.getText());
            t.setFont(f);
            glyphMetricArea.getChildren().add(t);

            if (showTextOrigin.isSelected()) {
                Circle c = new Circle(xpos, 100, 2, Color.BLUE);
                glyphMetricArea.getChildren().add(c);
            }

            Bounds bl = t.getBoundsInLocal();
            System.err.println("LOGICAL:" + bl);
            if (showLogicalBounds.isSelected()) {
                Rectangle r = new Rectangle(bl.getMinX(), bl.getMinY(), bl.getWidth(), bl.getHeight());
                r.setFill(null);
                r.setStroke(Color.GRAY);
                r.getStrokeDashArray().addAll(2.0, 2.0);
                glyphMetricArea.getChildren().add(r);
            }
    
            t.setBoundsType(TextBoundsType.VISUAL);
            
            Bounds bl2 = t.getBoundsInLocal();
            System.err.println("VISUAL:" + bl2);
            if (showVisualBounds.isSelected()) {
                Rectangle r2 = new Rectangle(bl2.getMinX(), bl2.getMinY(), bl2.getWidth(), bl2.getHeight());
                r2.setFill(null);
                r2.setStroke(Color.ORANGE);
                r2.getStrokeDashArray().addAll(5.0, 5.0);
                glyphMetricArea.getChildren().add(r2);
            }
    
            t.setBoundsType(TextBoundsType.LOGICAL);
            
            xpos += bl.getWidth();
        });

        return glyphMetricArea;
    }


    public Bounds reportSize(String s, Font myFont) {
        Text text = new Text(s);
        // text.setBoundsType(TextBoundsType.VISUAL);
        text.setFont(myFont);
        //text.setY(0);
        // Bounds tb = text.getBoundsInLocal();
        text.setTextOrigin(VPos.TOP);

        Bounds tb = text.getBoundsInLocal();
        System.err.println("TEXT BOUNDS: " + tb);
        Rectangle stencil = new Rectangle(
                tb.getMinX(), tb.getMinY(), tb.getWidth(), tb.getHeight()
        );
        //System.err.println("STENCIL BOUNDS: " + stencil.getBoundsInLocal());

        Shape intersection = Shape.intersect(text, stencil);
        
        Bounds ib = intersection.getBoundsInLocal();
        System.err.println("RESULT BOUNDS: " + ib + "/" + intersection);
        return ib;
//        System.out.println(
//                "Text size: " + ib.getWidth() + ", " + ib.getHeight()
//        );
    }
    
    
    
    class GlyphBoundsInfo {
        private Bounds logicalBounds;
        private Bounds visualBounds;

        public GlyphBoundsInfo(Bounds bLogical, Bounds bVisual) {
            this.logicalBounds = bLogical;
            this.visualBounds = bVisual;
            System.err.println("1:" + bLogical);
            System.err.println("2:" + bVisual);
        }

        public Bounds getLogicalBounds() {
            return logicalBounds;
        }
        
        public Bounds getVisualBounds() {
            return visualBounds;
        }

        @Override
        public String toString() {
            return String.format("GlyphBoundsInfo[ %s/%s,%sx%s / %s/%s,%sx%s]",  
                                 logicalBounds.getMinX(), logicalBounds.getMinY(), logicalBounds.getWidth(), logicalBounds.getHeight(),
                                 visualBounds.getMinX(), visualBounds.getMinY(), visualBounds.getWidth(), visualBounds.getHeight());
        }
    }

    private GlyphBoundsInfo getGlyphBounds(String s, Font font) {
        Pane p = new Pane();
        Text text = new Text(s);
        text.setFont(font);
        text.setTextOrigin(VPos.BASELINE);
        text.setBoundsType(TextBoundsType.LOGICAL);

        p.getChildren().add(text);
        
        Bounds bLogical = text.getBoundsInLocal();

        text.setBoundsType(TextBoundsType.VISUAL);
        Bounds bVisual = text.getBoundsInLocal();

        return new GlyphBoundsInfo(bLogical, bVisual);
    }

    private class GlyphData {
        char character;         // the character
        float charWidth;        // the width of the character
        float charHeight;       // the line height (same value for each glyph)
        Bounds charBox;         // the bounding box of the visible part of the glyph (might be much smaller than charWidth X charHeight)
        Pane glyphNode; 		// the javafx node which renders the glyph
        String glyphId;			// the C identifier for this glyph 
        GlyphBoundsInfo gbi;

        @Override
        public String toString() {
            return "" + character;
        }
    }

    private void updateGlyphs2() {
        glyphs.getChildren().clear();
        
        Text c = new Text(0, 0 , inputLine.getText());
        c.setTextOrigin(VPos.TOP);
        c.setFont(f);
        c.setFill(Color.RED);
        Pane gg = new Pane();
        gg.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        gg.getChildren().add(c);
        glyphs.getChildren().add(gg);
    }

    // private float xpos = 0;
    private float yMin = 0;
    private float yMax = 0;
    private void updateGlyphs3() {

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
            gd.gbi = getGlyphBounds(String.valueOf((char) e), f);
            System.err.println(gd.gbi);
            gd.character =  (char) e;

            if (gd.charBox.getMinY() < yMin) {
                yMin = (float) gd.charBox.getMinY();
            }
            if (gd.charBox.getMaxY() > yMax) {
                yMax = (float) gd.charBox.getMaxY();
            }

            glyphData.add(gd);
        });

        glyphs.getChildren().clear();
        glyphs.setSpacing(0);
        glyphs.setPadding(Insets.EMPTY);
        final float charHeight = yMax - yMin;
        float xpos = 0;
        int i = 0;
        for (GlyphData gd : glyphData) {
            // background shape and container for glyph and bounding boxes
        	gd.charHeight = charHeight;
            Pane gg = new Pane();
            gg.setPrefWidth(gd.charWidth);
            gg.setPrefHeight(gd.charHeight);
            //gg.setMPrefWidth(gd.charWidth);
            gg.setMaxHeight(gd.charHeight);
            glyphs.setPadding(Insets.EMPTY);
            //gg.setCenterShape(false);
            gg.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
//            if (i % 2 == 0 ) {
//            	gg.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
//            } else { 
//            	gg.setBackground(new Background(new BackgroundFill(Color.YELLOW, null, null)));
//        	}
//            i++;
        

//            final Rectangle r = new Rectangle(xpos,  yMin, gd.charWidth, charHeight);
//            r.setFill(Color.BLACK);
////            r.setFill(null);
////            r.setStroke(Color.BLACK);
////            r.getStrokeDashArray().add(0, 5.0);
////            r.getStrokeDashArray().add(1, 5.0);
//            gg.getChildren().add(r);

        // Bounding box of the character
            if (showVisualBounds.isSelected()) {
              final Rectangle gr = new Rectangle(gd.gbi.getVisualBounds().getMinX(),
                                                 gd.gbi.getVisualBounds().getMinY(),
                                                 gd.gbi.getVisualBounds().getWidth(),
                                                 gd.gbi.getVisualBounds().getHeight());
//                final Rectangle gr = new Rectangle(gd.charBox.getMinX(), gd.charBox.getMinY(),
//                                                   gd.charBox.getWidth(), gd.charBox.getHeight());
                gr.setManaged(false);
                //gr.setFill(Color.BLACK);
                //gr.setStroke(null);
                gr.setFill(null);
                gr.setStroke(Color.WHITE);
                gr.getStrokeDashArray().add(0, 5.0);
                gr.getStrokeDashArray().add(1, 5.0);
                gg.getChildren().add(gr);
            }
            if (showLogicalBounds.isSelected()) {
                final Rectangle gr = new Rectangle(gd.gbi.getLogicalBounds().getMinX(),
                                                   gd.gbi.getLogicalBounds().getMinY(),
                                                   gd.gbi.getLogicalBounds().getWidth(),
                                                   gd.gbi.getLogicalBounds().getHeight());

                //final Rectangle gr = new Rectangle(0, gd.charBox.getMinY(),
                //        						   gd.charBox.getWidth(), gd.charBox.getHeight());
                // final Rectangle gr = new Rectangle(0, 0, 30, 30);
                gr.setManaged(false);
                gr.setFill(null);
                gr.setStroke(Color.RED);
                gr.getStrokeDashArray().add(0, 5.0);
                gr.getStrokeDashArray().add(1, 5.0);
                gg.getChildren().add(gr);
            }

            // glyph - position is relative to Pane!
            Text c = new Text(0, 0 , /* gd.charHeight, */String.valueOf(gd.character));
            c.setManaged(false);
            // c.setBoundsType(TextBoundsType.LOGICAL_VERTICAL_CENTER);
            c.setTextOrigin(VPos.TOP);  // Default is VPos.BASELINE!!!!
            c.setFont(f);
            c.setFill(Color.RED);
            gg.getChildren().add(c);

            
            gd.glyphNode = gg;
            glyphs.getChildren().add(gg);

            xpos += gd.gbi.getLogicalBounds().getWidth(); //  gd.charWidth; //  + 4;
            

            // c.setBoundsType(TextBoundsType.VISUAL);
            
            System.err.println(c.getBoundsInParent());
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
}
