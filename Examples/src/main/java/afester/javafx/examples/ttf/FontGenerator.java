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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import afester.javafx.examples.Example;
import afester.javafx.examples.image.ArrayDump;
import afester.javafx.examples.image.ImageConverter;
import afester.javafx.examples.image.JavaArrayDump;
import afester.javafx.examples.image.RleEncoder;
import afester.javafx.examples.ttf.MedianCut.MyColor;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;



enum ExportFileFormat implements RadioButtonValues {
    C_CODE("C-Code"),
    BINARY("Binary");


    private String label;

    ExportFileFormat(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }
}


enum ExportImageFormat implements RadioButtonValues {
    PNG("PNG"), 
    RGB565_INDEXED("RGB565 Indexed"),
    RGB565("RGB565"), 
    RGB565_COMPRESSED("RGB565 Compressed"),
    INDEXED_4("4 bit indexed");

    private String label;

    ExportImageFormat(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }
}



enum ColorReduction implements RadioButtonValues {
    NONE("None"), 
    RGB565("RGB 565"),
    COLORS_16("16 Colors"); 

    private String label;

    ColorReduction(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }
}


/**
 * Example for loading a custom true type font
 */
@Example(desc = "Custom True Type Font",
         cat  = "Basic JavaFX")
public class FontGenerator extends Application {
 
    public static void main(String[] args) {
        // The following properties are required on Linux for improved text rendering
//        System.setProperty("prism.lcdtext", "false");
//        System.setProperty("prism.text", "t2k");
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    private TextField inputLine;
    private Font currentFont;
    private Color currentColor;

    private PaletteView pv;
    private ColorPalette cp; 
    private List<GlyphData> glyphData = new ArrayList<>();
    private CheckBox showVisualBounds;
    private CheckBox showLogicalBounds;
    private CheckBox showTextOrigin;
    private CheckBox showTopBound;
    private CheckBox showBottomBound;
    private CheckBox showEffectiveBounds;
    private HBox snapshots;

    private RadioButtonGroup<ExportFileFormat> exportSelection;
    private RadioButtonGroup<ExportImageFormat> formatSelection;
    private RadioButtonGroup<ColorReduction> reduction;

    //private ExportFileFormat exportFileFormat = ExportFileFormat.C_CODE;
    //private ExportImageFormat exportImageFormat = ExportImageFormat.PNG;

//    private class Digit extends Group {
//
//        public Digit(int value) {
//            Text background = new Text("8.");
//            background.setTextOrigin(VPos.TOP);
//            background.setFont(f);
//            background.setFill(new Color(0.15, 0.15, 0.15, 1.0));
//
////            Bounds b = background.getLayoutBounds();
////            System.err.printf("%s/%s\n", b.getWidth(), b.getHeight());
////            Rectangle r = new Rectangle(b.getWidth() + 6, b.getHeight());
////            r.setFill(Color.YELLOW);
//
//            Text sampleText = new Text(Integer.toString(value));
//            sampleText.setTextOrigin(VPos.TOP);
//            sampleText.setFont(f);
//            sampleText.setFill(Color.RED);
//
//            getChildren().addAll(/*r,*/ background, sampleText);
//        }
//    }
    
    
    @Override
    public void start(Stage stage) {
        stage.setTitle("Font Generator");

        Text t = new Text();

        InputStream is = getClass().getResourceAsStream("DSEG7Classic-BoldItalic.ttf");
        currentFont = Font.loadFont(is, 72);
        currentColor = Color.RED;
        // f = Font.font("Arial", FontWeight.EXTRA_BOLD, FontPosture.ITALIC, 12.0);

        FontSelectionPanel fsp = new FontSelectionPanel(currentFont);
        fsp.setOnFontChanged( e-> {
            System.err.println(e);
            currentFont = Font.font(e.getFamily()); // , e.getWeight(), e.isItalic() ? FontPosture.ITALIC : FontPosture.REGULAR, e.getSize());
            System.err.println(currentFont);
            t.setFont(currentFont);
            updateGlyphs();
        });

        inputLine = new TextField("0123456789,ACTUV:�");
        inputLine.textProperty().addListener((obj, oldVal, newVal) -> {
            t.setText(newVal);
            updateGlyphs();
        });

        t.setText(inputLine.getText());
        t.setFont(currentFont);

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

        showTopBound = new CheckBox("Show top bound");
        showTopBound.setOnAction(e -> {
            updateGlyphs();
        });

        showBottomBound = new CheckBox("Show bottom bound");
        showBottomBound.setOnAction(e -> {
            updateGlyphs();
        });

        showEffectiveBounds = new CheckBox("Show effective bounds");
        showEffectiveBounds.setOnAction(e -> {
            updateGlyphs();
        });

        final ColorPicker colorPicker = new ColorPicker(Color.RED);
        colorPicker.setOnAction(e -> {
            currentColor = colorPicker.getValue(); 
            updateGlyphs();
        });

        snapshots = new HBox();
        snapshots.setSpacing(2);

        HBox previewOptions = new HBox();
        pv = new PaletteView();
        previewOptions.getChildren().add(pv);

        reduction = new RadioButtonGroup<>(ColorReduction.values());
        reduction.setOnAction(e -> updatePreview(pv) );

//        reduceRGB565 = new CheckBox("Reduce to RGB565");
        previewOptions.getChildren().add(reduction);
//        reduceRGB565.setOnAction(e -> {
//            updatePreview(pv);
//        });

        Button previewButton = new Button("Preview");
        previewButton.setOnAction(e -> {
            updatePreview(pv);
        });

        Button exportButton = new Button("Export ...");
        exportButton.setOnAction(e -> {

            switch (this.exportSelection.getSelectedValue()) {
                case BINARY:
                    switch(this.formatSelection.getSelectedValue()) {
                        case INDEXED_4:
                            break;
                        case PNG:
                            exportBinaryPNG();
                            break;
                        case RGB565:
                            exportBinaryRGB565();
                            break;
                        case RGB565_COMPRESSED:
                            exportBinaryRGB565Compressed();
                            break;
                        case RGB565_INDEXED:
                            break;
                    }
                    break;

                case C_CODE:
                    switch(this.formatSelection.getSelectedValue()) {
                        case INDEXED_4:
                            exportCCode4bitIndexed();
                            break;

                        case PNG:
                            exportCCodePNG();
                            break;
                        case RGB565:
                            exportCCodeRGB565();
                            break;
                        case RGB565_COMPRESSED:
                            exportCCodeRGB565Compressed();
                            break;
                        case RGB565_INDEXED:
                            exportCCodeRGB565Indexed();
                            break;
                    }
                    break;
            }

//            try {
//                FileOutputStream fos = new FileOutputStream("font.c");
//                PrintStream out = new PrintStream(fos);
//
//                out.println("#include <stddef.h>");
//                out.println("#include <ILI9481.h>");
//                out.println("#include <avr/pgmspace.h>");
//                out.println();
//
//                // Create the glyph bitmap data
//                int glyphIdx = 0;
//                glyphData.sort( (a, b) -> { return a.character > b.character ? 1 : -1; } );
//                Map<Integer, GlyphData> charSetMap = new HashMap<>();
//                RleEncoder rle = new RleEncoder();
//                for (GlyphData gd : glyphData) {
//
////......
////		            int newLength = rle.rleEncode_4plus4(bitmap);
////		            byte[] bitmapRle = new byte[newLength];
////		            System.arraycopy(bitmap, 0, bitmapRle, 0, newLength);
////		            
////		            ArrayDump ad = new ArrayDump(bitmapRle);
////		            int width = (int) img.getWidth();
////		            int height = (int) img.getHeight();
////
////		            out.printf("const Bitmap8 %s PROGMEM = {%s, %s,\n", gd.glyphId, width, height);
////		            ad.dumpAll(width, out);
////		            out.println("};\n");
///////////////////
//                    
////                    System.err.printf("%s X %s\n", img.getWidth(), img.getHeight());
////                    stage.sizeToScene();
////                    
////                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
////                    ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", baos);
////    
////                    byte[] rgb565 = ic.getRGB565asByte(img);
////
////                    
////                    
////		            int newLength = ImageConverter.rleEncode(rgb565);
////		            byte[] compressed= new byte[newLength];
////		            System.arraycopy(rgb565, 0, compressed, 0, newLength);
////
////                    //byte[] compressed = compressRLE(rgb565);
////                    System.err.println("Compressed size:" + compressed.length);
////
////                    out.println(String.format("// Glyph data: '%c'", gd.character));
////                    gd.glyphId = String.format("g%03d", glyphIdx++);
////                    charSetMap.put((int) gd.character, gd);
////                    
////                    //out.println(String.format("uint8_t %s = ", gd.glyphId));
////                    out.println(String.format("static const  Bitmap8 %s PROGMEM = {%s, %s,",
////                    		gd.glyphId, (int) img.getWidth(), (int) img.getHeight()));
////                    		//gd.glyphId, (int) gd.charWidth, (int) gd.charHeight));
////                    ArrayDump cd = new ArrayDump(compressed);
////                    cd.dumpAll(out);
////                    out.println("};\n");
//                }
//
//                // create the font array
//                //out.println("struct fontData {");
//                //out.println("  uint8_t *glyph;");
//                //out.println("  uint8_t width");
//                //out.println("};");
//                //out.print("struct fontData charSet[224] = {");
//                out.print("const Bitmap8* charSet[224] = {");
//                for (int idx = ' ';  idx < 256;  idx++) {
//
//                    GlyphData gd = charSetMap.get(idx);
//                    String glyphId = "NULL";
//                    int glyphWidth = 0;
//                    if (gd != null) {
//                        glyphId = "&" + gd.glyphId;
//                        glyphWidth = (int) gd.charWidth;
//                    }
//
//                    if (idx > ' ') {
//                        out.print(", ");
//                    }
//                    if ((idx % 8) == 0) {
//                        out.println();
//                    }
//
//                    //out.print(String.format("{%s /*%03d'%c'*/, %d}", glyphId, idx, (char) idx, glyphWidth));
//                    out.print(String.format("%s /*%03d'%c'*/", glyphId, idx, (char) idx));
//                }
//                out.println("};");
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
        });

        bottomBox.getChildren().addAll(showVisualBounds, showLogicalBounds, showTextOrigin, 
                                       showTopBound, showBottomBound, showEffectiveBounds, colorPicker);

//        glyphs = new HBox();

        glyphMetricArea = new Pane();
        glyphMetricArea.setStyle("-fx-background-color: black");
        updateGlyphs();

//##############################        
        HBox exportComponent = new HBox();
        exportComponent.setSpacing(10);

        exportSelection = new RadioButtonGroup<>(ExportFileFormat.values());
        exportSelection.setSelectedValue(ExportFileFormat.C_CODE);
        formatSelection = new RadioButtonGroup<>(ExportImageFormat.values());
        formatSelection.setSelectedValue(ExportImageFormat.PNG);

        exportComponent.getChildren().addAll(exportSelection, formatSelection, exportButton);
//##############################
        VBox box = new VBox();
        box.setSpacing(10);
        
//        
//        ColorPalette cp = new ColorPalette();
//        Random rnd = new Random();
//        for (int i = 0;  i < 1000;  i++) {
//            Color c = new Color(rnd.nextDouble(), rnd.nextDouble(), rnd.nextDouble(), 1.0);
//            cp.addColor(c);
//        }
//        PaletteView colorSortSample = new PaletteView(cp);

        box.getChildren().addAll(fsp, inputLine, /*t,*/ glyphMetricArea, bottomBox, previewButton, snapshots, previewOptions, exportComponent);
                                 // colorSortSample);

                                 //glyphMetricArea); // , b, saveBtn, disp, snapshotBtn, snapshots);// , colorPicker);

        Scene scene  = new Scene(box);

        stage.setScene(scene);
        stage.show();
    }



    private void updatePreview(PaletteView pv) {
        SnapshotParameters params = new SnapshotParameters();
        snapshots.getChildren().clear();
        
        //ImageConverter ic = new ImageConverter();
        /*ColorPalette */ cp = new ColorPalette();

        List<Image> allImages = new ArrayList<>();
        for (GlyphData gd : glyphData) {
            Bounds b = glyphMetricArea.getBoundsInParent();
            params.setViewport(new Rectangle2D(b.getMinX() + gd.effectiveBounds.getMinX(), 
                                               b.getMinY() + gd.effectiveBounds.getMinY(),
                                               gd.effectiveBounds.getWidth(), gd.effectiveBounds.getHeight()));
            gd.glyphImg = glyphMetricArea.snapshot(params, null);
            allImages.add(gd.glyphImg);
        }

        switch(reduction.getSelectedValue()) {
    
            case COLORS_16: {
                    // Now quantize the SET of images
                    MedianCut mc = new MedianCut();
        
                    // Step 1: Get a map of all colors in the original image
                    Map<Integer, MyColor> origCols = mc.findOriginalColors(allImages);
                    System.err.println("Number of colors: " + origCols.size());
        
                    // Step 2: find the colors which best match all the original colors in the reduced palette
                    int[] repCols = mc.findRepresentativeColors(origCols, 16);
                    System.err.println("Number of rep colors: " + repCols.length);
        
                    // Step 3: Quantize all images with the new set of colors
                    for (GlyphData gd : glyphData) {
                        gd.glyphImg = mc.quantizeImage(gd.glyphImg, repCols);
        
                        ImageView iv = new ImageView(gd.glyphImg);
                        snapshots.getChildren().add(iv);
                        cp.addColors(gd.glyphImg);
                    }
        
                }
                break;

            case NONE:
                for (GlyphData gd : glyphData) {
                    ImageView iv = new ImageView(gd.glyphImg);
                    snapshots.getChildren().add(iv);
                    cp.addColors(gd.glyphImg);
                }
                break;

            case RGB565:
                /// gd.glyphImg = reduceColorsTo565(gd.glyphImg);
                break;

            default:
                break;
        }

        System.err.println(cp.getColorList());
        pv.setPalette(cp);
    }


	/**
     * Reduces the colors in the given image to the RGB565 format.
     *
     * @param glyphImg The image to reduce.
     * @return The image with the reduced number of colors.
     */
    private Image reduceColorsTo565(Image glyphImg) {
        WritableImage outputImage = new WritableImage((int) glyphImg.getWidth(), (int) glyphImg.getHeight());
        PixelReader reader = glyphImg.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();

        for (int y = 0; y < glyphImg.getHeight(); y++) {
           for (int x = 0; x < glyphImg.getWidth(); x++) {
              int argb = reader.getArgb(x, y);
              argb = argb & 0xFFF8FCF8;     // 5 bits red, 6 bits green, 5 bits blue
              writer.setArgb(x, y, argb);
           }
        }

        return outputImage;
    }

//    private void getColors(Image glyphImg) {
//        Set<Color> colors = new HashSet<>();
//        PixelReader pr = glyphImg.getPixelReader();
//        for (int y = 0;  y < glyphImg.getHeight();  y++) {
//            for (int x = 0;  x < glyphImg.getWidth();  x++) {
//                Color c = pr.getColor(x, y);
//                colors.add(c);
//            }
//        }
//        
//        System.err.println("Number of colors:" + colors.size());
//    }

    private void exportCCodeRGB565Compressed() {
        String fileName = "font.c";
        System.err.print("Export " + fileName);

        int overallSize = 0;
        try (PrintStream out = new PrintStream(new FileOutputStream(fileName))) {
            ImageConverter ic = new ImageConverter();
            RleEncoder rle = new RleEncoder();
            List<Short> palette = new ArrayList<>();

            // calculate the palette size
            for (GlyphData gd : glyphData) {
    
                short[] rgb565 = ic.getRGB565(gd.glyphImg);
    
                // convert bitmap to indexed bitmap
//                byte[] bitmap = new byte[rgb565.length];
                for (int idx = 0;  idx < rgb565.length;  idx++) {
                    short value = rgb565[idx];
            
                    // lookup value index
                    int colorIdx = palette.indexOf(value);
                    if (colorIdx == -1) {
                        colorIdx = palette.size();
                        palette.add(value);
                    }

//                    bitmap[idx] = (byte) colorIdx;  // TODO: max. 256 colors
                }
            }
            System.err.println("Palette size: " + palette.size());
            System.err.println(palette);

            // Reduce the number of colors
            // Since a color is represented in three dimensions (R, G, B) we can't simply sort them 
            // into a linear array and see what elements are adjacent
            // https://javagraphics.blogspot.com/2014/02/images-color-palette-reduction.html

//                int compressedSize = rle.rleEncode_4plus4(bitmap);
//                byte[] result = new byte[compressedSize];
//                System.arraycopy(bitmap, 0, result, 0, compressedSize);
//                overallSize += result.length;
//
//                out.printf("const Bitmap8Rle %s PROGMEM = {%d, %d,\n", gd.glyphId, (int) gd.glyphImg.getWidth(), (int) gd.glyphImg.getHeight());
//                ArrayDump ad = new ArrayDump(result);
//                ad.dumpAll(16, out);
//                out.println("};\n");
//            }

            out.printf("uint16_t palette[] =\n");
            ArrayDump ad2 = new ArrayDump(palette);
            ad2.dumpAll(16, out);
            out.println(";");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.err.printf("Overall size: %s bytes\n", overallSize);

    }

    private void exportCCodeRGB565Indexed() {
        String fileName = "font.c";
        System.err.print("Export " + fileName);

        int overallSize = 0;
        try (PrintStream out = new PrintStream(new FileOutputStream(fileName))) {
            ImageConverter ic = new ImageConverter();
            List<Short> palette = new ArrayList<>();

            for (GlyphData gd : glyphData) {
    
                short[] rgb565 = ic.getRGB565(gd.glyphImg);
    
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
            
                    bitmap[idx] = (byte) colorIdx;  // TODO: max. 256 colors
                }
                overallSize += bitmap.length;

                out.printf("const Bitmap8 %s PROGMEM = {%d, %d,\n", gd.glyphId, (int) gd.glyphImg.getWidth(), (int) gd.glyphImg.getHeight());
                ArrayDump ad = new ArrayDump(bitmap);
                ad.dumpAll(16, out);
                out.println("};\n");
            }

            out.printf("uint16_t palette[] =\n");
            ArrayDump ad2 = new ArrayDump(palette);
            ad2.dumpAll(16, out);
            out.println(";");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.err.printf("Overall size: %s bytes\n", overallSize);
    }

    private void exportCCode4bitIndexed1() {
        String fileName = "font.c";
        System.err.print("Export " + fileName);

        int overallSize = 0;
        try (PrintStream out = new PrintStream(new FileOutputStream(fileName))) {
		out.printf("/* Font: %s %s pt */\n\n", currentFont.getName(), currentFont.getSize());
        	out.printf("#include <stdlib.h>\n"
        			  +"#include \"ILI9481.h\"\n"
        			  +"#include \"avr/pgmspace.h\"\n\n");
        	
/**************** Export the glyph images */
            Map<Integer, GlyphData> charSetMap = new HashMap<>();
            ImageConverter ic = new ImageConverter();
            for (GlyphData gd : glyphData) {

                byte[] indexedBitmap = ic.getIndexed(gd.glyphImg, cp);
                RleEncoder re = new RleEncoder();
                int newSize = re.rleEncode_4plus4(indexedBitmap);
                byte[] compressed = new byte[newSize];
                System.arraycopy(indexedBitmap, 0, compressed, 0, newSize);
                overallSize += compressed.length;

                charSetMap.put((int) gd.character, gd);

                out.printf("const Bitmap8 %s PROGMEM = {%d, %d,\n", gd.glyphId, (int) gd.glyphImg.getWidth(), (int) gd.glyphImg.getHeight());
                ArrayDump ad = new ArrayDump(compressed);
                ad.dumpAll(16, out);
                out.println("};\n");
            }

/**************** Export the palette */
            System.err.println();
            int idx1 = 0;
            for (Color c : cp.getColorList()) {
            	System.err.printf("%s -> %s -> %s\n",  idx1, c, cp.indexOf(c));
            	idx1++;
            }

            // Export the palette values (the actual color values) in RGB565 format
            out.printf("const uint16_t palette[] PROGMEM =\n{");
            boolean first = true;
            for (Color c : cp.getColorList()) {
                if (!first) {
                    out.printf(", ");
                }
                first = false;
                int colorValue = MedianCut.col2int(c);
                short rgb565Value = ImageConverter.fromARGBToRGB565(colorValue);
                out.printf("0x%04x", rgb565Value);
            }
            out.println("};\n");

/**************** Create ASCII to glyph mapping */
          out.print("const Bitmap8* const charSet[224] PROGMEM = {");
          for (int idx = ' ';  idx < 256;  idx++) {
              GlyphData gd = charSetMap.get(idx);

              String glyphId = "NULL";
              if (gd != null) {
                  glyphId = "&" + gd.glyphId;
              }

              if (idx > ' ') {
                  out.print(", ");
              }
              if ((idx % 8) == 0) {
                  out.println();
              }

              out.print(String.format("%-4s /*%03d'%c'*/", glyphId, idx, (char) idx));
          }
          out.println("};");

/********************/

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.err.printf("Overall size: %s bytes\n", overallSize);
    }

    // private void exportJavaCode4bitIndexed() {
    private void exportCCode4bitIndexed() {
        String fileName = "src/main/java/Glyph.java";
        System.err.print("Export " + fileName);

//        int overallSize = 0;
        try (PrintStream out = new PrintStream(new FileOutputStream(fileName))) {

            out.printf("class Font {\n");
            
/**************** Export the glyph images */
            Map<Integer, GlyphData> charSetMap = new HashMap<>();
            ImageConverter ic = new ImageConverter();
//            for (GlyphData gd : glyphData) {
                GlyphData gd = glyphData.get(0);

                byte[] indexedBitmap = ic.getIndexed(gd.glyphImg, cp);
                RleEncoder re = new RleEncoder();
                int newSize = re.rleEncode_4plus4(indexedBitmap);
                byte[] compressed = new byte[newSize];
                System.arraycopy(indexedBitmap, 0, compressed, 0, newSize);
//                overallSize += compressed.length;

                charSetMap.put((int) gd.character, gd);

                out.printf("    public final int WIDTH = %d;\n"
                        +  "    public final int HEIGHT = %d;\n"
                        +  "    public final byte[] %s = \n", (int) gd.glyphImg.getWidth(), (int) gd.glyphImg.getHeight(), gd.glyphId);
                ArrayDump ad = new JavaArrayDump(compressed);
                ad.dumpAll(16, out);
                out.println(";\n");
//            }

/**************** Export the palette */
            // Export the palette values (the actual color values) in RGB565 format
            out.printf("public final short[] palette =\n{");
            boolean first = true;
            for (Color c : cp.getColorList()) {
                if (!first) {
                    out.printf(", ");
                }
                first = false;
                int colorValue = MedianCut.col2int(c);
                short rgb565Value = ImageConverter.fromARGBToRGB565(colorValue);
                out.printf("(short) 0x%04x", rgb565Value);
            }
            out.println("};\n");

/**************** Create ASCII to glyph mapping */
//          out.print("const Bitmap8* const charSet[224] PROGMEM = {");
//          for (int idx = ' ';  idx < 256;  idx++) {
//              GlyphData gd = charSetMap.get(idx);
//
//              String glyphId = "NULL";
//              if (gd != null) {
//                  glyphId = "&" + gd.glyphId;
//              }
//
//              if (idx > ' ') {
//                  out.print(", ");
//              }
//              if ((idx % 8) == 0) {
//                  out.println();
//              }
//
//              out.print(String.format("%-4s /*%03d'%c'*/", glyphId, idx, (char) idx));
//          }

          out.println("}");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void exportCCodeRGB565() {
        String fileName = "font.c";
        System.err.print("Export " + fileName);

        int overallSize = 0;
        try (PrintStream out = new PrintStream(new FileOutputStream(fileName))) {
            ImageConverter ic = new ImageConverter();
            for (GlyphData gd : glyphData) {
                short[] rgb565 = ic.getRGB565(gd.glyphImg);
                System.err.println(" =>" + rgb565.length);
                overallSize += rgb565.length*2;

                out.printf("const Bitmap16 %s PROGMEM =\n", gd.glyphId);
                ArrayDump ad = new ArrayDump(rgb565);
                ad.dumpAll(16, out);
                out.println(";\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } 
        System.err.printf("Overall size: %s bytes\n", overallSize);
    }

    private void exportCCodePNG() {
        String fileName = "font.c";
        System.err.print("Export " + fileName);

        int overallSize = 0;
        try (PrintStream out = new PrintStream(new FileOutputStream(fileName))) {

            for (GlyphData gd : glyphData) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageIO.write(SwingFXUtils.fromFXImage(gd.glyphImg, null), "png", bos);
                System.err.println(" =>" + bos.size());
                overallSize += bos.size();

                out.printf("const PngFile %s PROGMEM =\n", gd.glyphId);
                ArrayDump ad = new ArrayDump(bos.toByteArray());
                ad.dumpAll(16, out);
                out.println(";\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.err.printf("Overall size: %s bytes\n", overallSize);
    }

    private void exportBinaryRGB565Compressed() {
    }

    private void exportBinaryRGB565() {
        ImageConverter ic = new ImageConverter();
        for (GlyphData gd : glyphData) {
            String fileName = gd.glyphId + ".rgb565";
            System.err.print("Export " + fileName);

            byte[] rgb565 = ic.getRGB565asByte(gd.glyphImg);
            try (FileOutputStream fos = new FileOutputStream(fileName)) {
                fos.write(rgb565);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }            
    }

    private void exportBinaryPNG() {
        for (GlyphData gd : glyphData) {
            String fileName = gd.glyphId + ".png";
            System.err.println("Export " + fileName);
            File theFile = new File(fileName);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(gd.glyphImg, null), "png", theFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Pane glyphMetricArea;

    double xpos = 10;
    double topY = 1000;
    double bottomY = 0;
    int theId = 0;

    private Pane updateGlyphs() {

        glyphMetricArea.getChildren().clear();
        glyphData.clear();

        xpos = 10;
        topY = 1000;
        bottomY = 0;

        //List<Bounds> effectiveBounds = new ArrayList<>();
        inputLine.getText().chars().forEach(e -> {
                
            final Text t = new Text(xpos, 100, String.valueOf((char) e));  // inputLine.getText());
            t.setFont(currentFont);
            t.setFill(currentColor);
            glyphMetricArea.getChildren().add(t);

            if (showTextOrigin.isSelected()) {
                Circle c = new Circle(xpos, 100, 2, Color.BLUE);
                glyphMetricArea.getChildren().add(c);
            }

            Bounds bl = t.getBoundsInLocal();
          //  System.err.println("LOGICAL:" + bl);
            if (showLogicalBounds.isSelected()) {
                Rectangle r = new Rectangle(bl.getMinX(), bl.getMinY(), bl.getWidth(), bl.getHeight());
                r.setFill(null);
                r.setStroke(Color.GRAY);
                r.getStrokeDashArray().addAll(2.0, 2.0);
                glyphMetricArea.getChildren().add(r);
            }
    
            t.setBoundsType(TextBoundsType.VISUAL);

            Bounds bl2 = t.getBoundsInLocal();
            System.err.println("\nVISUAL:" + bl2);
            System.err.println("  TOPY:" + topY);
            System.err.println("  VY:" + bl2.getMinY());
            topY = Math.min(topY,  bl2.getMinY());
            bottomY = Math.max(bottomY,  bl2.getMaxY());
            // System.err.println("  VISUAL:" + bl2);
            System.err.println("  TOPY:" + topY);
            if (showVisualBounds.isSelected()) {
                Rectangle r2 = new Rectangle(bl2.getMinX(), bl2.getMinY(), bl2.getWidth(), bl2.getHeight());
                r2.setFill(null);
                r2.setStroke(Color.ORANGE);
                r2.getStrokeDashArray().addAll(5.0, 5.0);
                glyphMetricArea.getChildren().add(r2);
            }
    
            t.setBoundsType(TextBoundsType.LOGICAL);

            GlyphData gd = new GlyphData();
            gd.character = (char) e;
            gd.logicalBounds = bl;
            glyphData.add(gd);
            xpos += bl.getWidth();
        });

        if (showTopBound.isSelected()) {
            Line l = new Line(0, topY, 2000, topY);
            l.setStroke(Color.RED);
            glyphMetricArea.getChildren().add(l);
        }

        if (showBottomBound.isSelected()) {
            Line l = new Line(0, bottomY, 2000, bottomY);
            l.setStroke(Color.RED);
            glyphMetricArea.getChildren().add(l);
        }

        theId = 0;
        glyphData.forEach(gd -> {
            gd.glyphId = "g" + (theId++);
            gd.effectiveBounds = new BoundingBox(gd.logicalBounds.getMinX(), topY, gd.logicalBounds.getWidth(), bottomY - topY);            
        });

        if (showEffectiveBounds.isSelected()) {
            glyphData.forEach(gd -> {
                Rectangle r2 = new Rectangle(gd.effectiveBounds.getMinX(), gd.effectiveBounds.getMinY(),
                                             gd.effectiveBounds.getWidth(), gd.effectiveBounds.getHeight());
                r2.setFill(null);
                r2.setStroke(Color.WHITE);
                // r2.getStrokeDashArray().addAll(5.0, 5.0);
                glyphMetricArea.getChildren().add(r2);
            });
        }

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
        Bounds logicalBounds;
        Bounds effectiveBounds;
        Image glyphImg;
        
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

//    private void updateGlyphs2() {
//        glyphs.getChildren().clear();
//        
//        Text c = new Text(0, 0 , inputLine.getText());
//        c.setTextOrigin(VPos.TOP);
//        c.setFont(f);
//        c.setFill(Color.RED);
//        Pane gg = new Pane();
//        gg.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
//        gg.getChildren().add(c);
//        glyphs.getChildren().add(gg);
//    }
//
//    // private float xpos = 0;
//    private float yMin = 0;
//    private float yMax = 0;
//    private void updateGlyphs3() {
//
//// Calculate all glyphs
//        glyphData.clear();
////xpos = 0;
//        yMin = 0;
//        yMax = 0;
//        final FontMetrics fm = Toolkit.getToolkit().getFontLoader().getFontMetrics(f);
//        // final float charHeight = fm.getXheight();    // height of non-capital "x" letter
//        ///final float charHeight = fm.getLineHeight();    // line height - might be much larger than the glyph sizes
//        inputLine.getText().chars().forEach(e -> {
//            GlyphData gd = new GlyphData();
//            gd.charWidth = fm.getCharWidth((char) e);  // width of the given character
//            gd.charBox = reportSize(String.valueOf((char) e), f);
//            gd.gbi = getGlyphBounds(String.valueOf((char) e), f);
//            System.err.println(gd.gbi);
//            gd.character =  (char) e;
//
//            if (gd.charBox.getMinY() < yMin) {
//                yMin = (float) gd.charBox.getMinY();
//            }
//            if (gd.charBox.getMaxY() > yMax) {
//                yMax = (float) gd.charBox.getMaxY();
//            }
//
//            glyphData.add(gd);
//        });
//
//        glyphs.getChildren().clear();
//        glyphs.setSpacing(0);
//        glyphs.setPadding(Insets.EMPTY);
//        final float charHeight = yMax - yMin;
//        float xpos = 0;
//        int i = 0;
//        for (GlyphData gd : glyphData) {
//            // background shape and container for glyph and bounding boxes
//        	gd.charHeight = charHeight;
//            Pane gg = new Pane();
//            gg.setPrefWidth(gd.charWidth);
//            gg.setPrefHeight(gd.charHeight);
//            //gg.setMPrefWidth(gd.charWidth);
//            gg.setMaxHeight(gd.charHeight);
//            glyphs.setPadding(Insets.EMPTY);
//            //gg.setCenterShape(false);
//            gg.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
////            if (i % 2 == 0 ) {
////            	gg.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
////            } else { 
////            	gg.setBackground(new Background(new BackgroundFill(Color.YELLOW, null, null)));
////        	}
////            i++;
//        
//
////            final Rectangle r = new Rectangle(xpos,  yMin, gd.charWidth, charHeight);
////            r.setFill(Color.BLACK);
//////            r.setFill(null);
//////            r.setStroke(Color.BLACK);
//////            r.getStrokeDashArray().add(0, 5.0);
//////            r.getStrokeDashArray().add(1, 5.0);
////            gg.getChildren().add(r);
//
//        // Bounding box of the character
//            if (showVisualBounds.isSelected()) {
//              final Rectangle gr = new Rectangle(gd.gbi.getVisualBounds().getMinX(),
//                                                 gd.gbi.getVisualBounds().getMinY(),
//                                                 gd.gbi.getVisualBounds().getWidth(),
//                                                 gd.gbi.getVisualBounds().getHeight());
////                final Rectangle gr = new Rectangle(gd.charBox.getMinX(), gd.charBox.getMinY(),
////                                                   gd.charBox.getWidth(), gd.charBox.getHeight());
//                gr.setManaged(false);
//                //gr.setFill(Color.BLACK);
//                //gr.setStroke(null);
//                gr.setFill(null);
//                gr.setStroke(Color.WHITE);
//                gr.getStrokeDashArray().add(0, 5.0);
//                gr.getStrokeDashArray().add(1, 5.0);
//                gg.getChildren().add(gr);
//            }
//            if (showLogicalBounds.isSelected()) {
//                final Rectangle gr = new Rectangle(gd.gbi.getLogicalBounds().getMinX(),
//                                                   gd.gbi.getLogicalBounds().getMinY(),
//                                                   gd.gbi.getLogicalBounds().getWidth(),
//                                                   gd.gbi.getLogicalBounds().getHeight());
//
//                //final Rectangle gr = new Rectangle(0, gd.charBox.getMinY(),
//                //        						   gd.charBox.getWidth(), gd.charBox.getHeight());
//                // final Rectangle gr = new Rectangle(0, 0, 30, 30);
//                gr.setManaged(false);
//                gr.setFill(null);
//                gr.setStroke(Color.RED);
//                gr.getStrokeDashArray().add(0, 5.0);
//                gr.getStrokeDashArray().add(1, 5.0);
//                gg.getChildren().add(gr);
//            }
//
//            // glyph - position is relative to Pane!
//            Text c = new Text(0, 0 , /* gd.charHeight, */String.valueOf(gd.character));
//            c.setManaged(false);
//            // c.setBoundsType(TextBoundsType.LOGICAL_VERTICAL_CENTER);
//            c.setTextOrigin(VPos.TOP);  // Default is VPos.BASELINE!!!!
//            c.setFont(f);
//            c.setFill(Color.RED);
//            gg.getChildren().add(c);
//
//            
//            gd.glyphNode = gg;
//            glyphs.getChildren().add(gg);
//
//            xpos += gd.gbi.getLogicalBounds().getWidth(); //  gd.charWidth; //  + 4;
//            
//
//            // c.setBoundsType(TextBoundsType.VISUAL);
//            
//            System.err.println(c.getBoundsInParent());
//        }
//
//        //Line l1 = new Line(0, yMin, xpos, yMin);
//        //Line l2 = new Line(0, yMax, xpos, yMax);
//        //glyphs.getChildren().addAll(l1, l2);
//    }
//
//    private String getImageType(int type) {
//        String result = "UNKNOWN";
//        switch(type) {
//            case BufferedImage.TYPE_CUSTOM  : result = "TYPE_CUSTOM"; break;
//            case BufferedImage.TYPE_INT_RGB  : result = "TYPE_INT_RGB"; break;
//            case BufferedImage.TYPE_INT_ARGB  : result = "TYPE_INT_ARGB"; break;
//            case BufferedImage.TYPE_INT_ARGB_PRE  : result = "TYPE_INT_ARGB_PRE"; break;
//            case BufferedImage.TYPE_INT_BGR  : result = "TYPE_INT_BGR"; break;
//            case BufferedImage.TYPE_3BYTE_BGR  : result = "TYPE_3BYTE_BGR"; break;
//            case BufferedImage.TYPE_4BYTE_ABGR  : result = "TYPE_4BYTE_ABGR"; break;
//            case BufferedImage.TYPE_4BYTE_ABGR_PRE  : result = "TYPE_4BYTE_ABGR_PRE"; break;
//            case BufferedImage.TYPE_USHORT_565_RGB  : result = "TYPE_USHORT_565_RGB"; break;
//            case BufferedImage.TYPE_USHORT_555_RGB  : result = "TYPE_USHORT_555_RGB"; break;
//            case BufferedImage.TYPE_BYTE_GRAY  : result = "TYPE_BYTE_GRAY"; break;
//            case BufferedImage.TYPE_USHORT_GRAY  : result = "TYPE_USHORT_GRAY"; break;
//            case BufferedImage.TYPE_BYTE_BINARY  : result = "TYPE_BYTE_BINARY"; break;
//            case BufferedImage.TYPE_BYTE_INDEXED  : result = "TYPE_BYTE_INDEXED"; break;
//        }
//        
//        return result;
//    }
}
