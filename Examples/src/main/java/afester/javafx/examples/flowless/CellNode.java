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

package afester.javafx.examples.flowless;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import org.fxmisc.flowless.Cell;

public class CellNode implements Cell<CellModel, Node> {

    private Node node;

    private Background createBackground(Color col) {
        return new Background(
                new BackgroundFill(col, new CornerRadii(0), new Insets(0)));
    }


    /**
     * Creates a new cell node from a cell model.
     *
     * @param contents  The cell model which defines the cell content.
     */
    public CellNode(CellModel contents) {

        // the rectangle which is shown at the left edge of each cell
        Rectangle rect = new Rectangle(14, 14);
        rect.setFill(contents.getRectColor());

        // A fixed width text at the beginning of the cell
        HBox prefixBox = new HBox();
        Text prefix = new Text(contents.getPrefix());
        prefix.setFont(Font.font("Courier New"));
        prefixBox.getChildren().add(prefix);

        // A text with a variable background color
        HBox textBox = new HBox();
        Text text = new Text(contents.getText());
        text.setFill(contents.getColor());
        textBox.getChildren().add(text);
        textBox.setBackground(createBackground(contents.getBgColor()));

        HBox panel = new HBox();
        panel.getChildren().addAll(rect, prefixBox, textBox);
        HBox.setMargin(rect,  new Insets(1, 2, 1, 2));
        
        node = panel;
    }


    @Override
    public Node getNode() {
        return node;
    }
    
    @Override
    public String toString() {
        return node.toString();
    }
}
