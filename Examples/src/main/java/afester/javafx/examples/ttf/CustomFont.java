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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

import com.example.hexdump.HexDump;

import afester.javafx.examples.Example;
import afester.javafx.examples.image.ArrayDump;
import afester.javafx.examples.image.ImageConverter;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


/**
 * Example for loading a custom true type font
 */
@Example(desc = "Custom True Type Font",
         cat  = "Basic JavaFX")
public class CustomFont extends Application {
 
    public static void main(String[] args) {
        // The following properties are required on Linux for improved text rendering
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override public void start(Stage stage) {
        stage.setTitle("True Type Font example");

        InputStream is = getClass().getResourceAsStream("DSEG7Classic-Bold.ttf");
        Font f = Font.loadFont(is, 64);

        Rectangle r = new Rectangle(53, 64);
        r.setFill(Color.BLACK);

//        Label background = new Label(" 8888888888");
//        Label background = new Label("8");
//        background.setFont(f);
//        background.setTextFill(new Color(0.1, 0.1, 0.1, 1.0));

//        Label sampleText = new Label(" .0123456789");
        Label sampleText = new Label("8");
        sampleText.setFont(f);
        sampleText.setTextFill(Color.RED);

        final ColorPicker colorPicker = new ColorPicker();
        colorPicker.setValue(Color.CORAL);
        colorPicker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                sampleText.setTextFill(colorPicker.getValue());               
            }
        });

        Group disp = new Group();
        disp.getChildren().addAll(r, /*background, */sampleText);

        VBox box = new VBox();
        Button b = new Button("Export");
        b.setOnAction(e -> {
            SnapshotParameters params = new SnapshotParameters();
            Image result = disp.snapshot(params, null);

            ImageConverter ic = new ImageConverter();
            byte[] rgb565 = ic.getRGB565(result);
            ArrayDump ad = new ArrayDump(rgb565);
            ad.dumpAll16(System.err, (int) result.getWidth());

            System.err.println();

            byte[] compressed = compressRLE(rgb565);
            System.err.println("Compressed size:" + compressed.length);
            ArrayDump cd = new ArrayDump(compressed);
            cd.dumpAll(System.err);

            // ad.dumpAll2(System.err);
        });

        Button saveBtn = new Button("Save as ...");
        saveBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Image file ...");
            File theFile = fileChooser.showSaveDialog(stage);
            if (theFile != null) {

                SnapshotParameters params = new SnapshotParameters();
                WritableImage wim = disp.snapshot(params, null);
                PixelFormat<?> pif = wim.getPixelReader().getPixelFormat();
                System.err.println("IMAGE TYPE: " + pif.getType());

                PixelFormat pf = PixelFormat.createByteIndexedInstance(new int[] {5, 10, 15});

                WritableImage img = new WritableImage(100, 100);
                System.err.println("NEW IMAGE TYPE: " + img.getPixelReader().getPixelFormat().getType());
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", theFile);

                    BufferedImage bi = SwingFXUtils.fromFXImage(wim, null);
                    System.err.println("IMAGE TYPE: " + getImageType(bi.getType()));

                    String[] available = ImageIO.getWriterFileSuffixes();
                    for (String a : available) {
                        System.err.println("  " + a);
                    }
                    Iterator<ImageWriter> wrs = ImageIO.getImageWritersByFormatName("png");
                    wrs.forEachRemaining(iw -> {
                        ImageWriteParam iwp;
                        System.err.println(iw);
                    });

                    ByteArrayOutputStream result = new ByteArrayOutputStream();
                    ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", result);

                    HexDump hd = new HexDump(result.toByteArray());
                    hd.dumpAll(System.err);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

//                try {
//                    FileInputStream input = new FileInputStream(theFile.getAbsolutePath());
//                    imageView.setImage(new Image(input));
//                    primaryStage.sizeToScene();
//                } catch (FileNotFoundException e1) {
//                    e1.printStackTrace();
//                }
            }

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
        });
        box.getChildren().addAll(b, saveBtn, disp, colorPicker);

        Scene scene  = new Scene(box);

        stage.setScene(scene);
        stage.show();
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
