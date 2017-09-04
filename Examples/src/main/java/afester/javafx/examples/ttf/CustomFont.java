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

import afester.javafx.examples.Example;
import afester.javafx.examples.image.ArrayDump;
import afester.javafx.examples.image.ImageConverter;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.ArrayList;


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
        Label background = new Label("8");
        background.setFont(f);
        background.setTextFill(new Color(0.1, 0.1, 0.1, 1.0));

//        Label sampleText = new Label(" .0123456789");
        Label sampleText = new Label("5");
        sampleText.setFont(f);
        sampleText.setTextFill(Color.RED);

        Group disp = new Group();
        disp.getChildren().addAll(r, background, sampleText);

        VBox box = new VBox();
        Button b = new Button("export");
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
        box.getChildren().addAll(b, disp);
       
        Scene scene  = new Scene(box);

        stage.setScene(scene);
        stage.show();
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
