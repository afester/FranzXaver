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

import afester.javafx.examples.Example;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.fxmisc.flowless.VirtualFlow;
import org.fxmisc.flowless.VirtualFlowHit;

import java.util.Random;


/**
 * Example for using the VirtualFlow from https://github.com/TomasMikula/Flowless
 */
@Example(desc = "Flowless example",
         cat  = "Third Party")
public class FlowlessExample extends Application {

    private ObservableList<CellModel> itemList = FXCollections.observableArrayList();
    private VirtualFlow<CellModel, CellNode> flowLayout; 

    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    private static Random randomColor = new Random(System.currentTimeMillis());

    private static Color randomColor() {
        return new Color(randomColor.nextFloat(), 
                         randomColor.nextFloat(), 
                         randomColor.nextFloat(), 1.0);
    }

    @Override
    public void start(Stage primaryStage) {

        Button addButton = new Button("Add Row");
        addButton.setOnAction(a -> addRow());
        //Button removeButton = new Button("Remove row");
        //removeButton.setOnAction(a -> removeRow());
        Button dumpButton = new Button("Dump Nodes");
        dumpButton.setOnAction(a -> dump());

        HBox buttons = new HBox();
        buttons.getChildren().addAll(addButton, /*removeButton,*/ dumpButton);

        VBox mainGroup = new VBox();
        mainGroup.getChildren().add(buttons);

        flowLayout = VirtualFlow.createVertical(itemList, data -> createCell(data));
        flowLayout.setOnMouseClicked(e -> mouseClicked(e));

        mainGroup.getChildren().add(flowLayout);
        VBox.setVgrow(flowLayout, Priority.ALWAYS);

        Scene scene = new Scene(mainGroup);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Object mouseClicked(MouseEvent e) {
        System.err.println("Mouse clicked:" + e.getX() + "/" + e.getY());
        VirtualFlowHit<CellNode> hit = flowLayout.hit(e.getX(), e.getY());
        if (hit.isAfterCells()) {
            System.err.printf("   AFTER\n");
        } else if (hit.isBeforeCells()) {
            System.err.printf("   BEFORE\n");
        } else {
            System.err.printf("   AT: %s\n", hit.getCell());
        }
        return null;
    }

    /**
     * Factory method to create a cell.
     * This method is called when the layout of the VirtualFlow changes and/or
     * new cells become visible. 
     * 
     * @param content The model data which is used to render the cell.
     * @return A CellNode object for the given model data.
     */
    private CellNode createCell(CellModel content) {
        //System.err.println("Creating cell...");
        return new CellNode(content);
    }

    private int row = 0;

    private void addRow() {
        String prefix = String.format("%08x:", row);
        itemList.add(new CellModel(prefix, "Lorem ipsum dolor sit amet", randomColor(), randomColor(), randomColor()));
        row++;
    }

    private void removeRow() {
        System.err.println("Removing row ...");
    }

    private void dump() {
        dumpNode(flowLayout, 0);
    }


    private void dumpNode(Parent parent, int level) {
        String prefix = "                ".substring(0, level*2);

        for (Node n : parent.getChildrenUnmodifiable()) {
            System.err.printf("%s%s\n", prefix, n);
            if (n instanceof Parent) {
                dumpNode((Parent) n, level + 1);
            }
        }
    }
}
