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

package afester.javafx.examples.charts;

import afester.javafx.examples.Example;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;


/**
 * Example for a styled line chart.
 */
@Example(desc = "Styled LineChart",
         cat  = "Basic JavaFX")
public class LinechartExample extends Application {
 
    public static void main(String[] args) {
        launch(args);
    }

    public void run() {
        start(new Stage());
    }

    @Override public void start(Stage stage) {
        stage.setTitle("Line Chart Sample");

        // defining the axes (NOTE: It is possible to use the same axis
        // for more than one chart - in that case, the axises are synchronized,
        // adding data to one chart which changes the scaling would adjust the other charts
        // axes as well
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Number of Month");

        //creating the chart
        final LineChart<Number,Number> lineChart = 
                new LineChart<Number,Number>(xAxis,yAxis);
        lineChart.setId("chart1");
        lineChart.setTitle("Chart 1");
        
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>(1, 23));
        series.getData().add(new XYChart.Data<>(2, 14));
        series.getData().add(new XYChart.Data<>(3, 15));

        final NumberAxis xAxis2 = new NumberAxis();
        final NumberAxis yAxis2 = new NumberAxis();

        final LineChart<Number,Number> lineChart2 = 
                new LineChart<Number,Number>(xAxis2,yAxis2);
        lineChart2.setId("chart2");
        lineChart2.setTitle("Chart 2");
        lineChart2.setCreateSymbols(false); // do not show the dots at each data point
        lineChart2.setAnimated(false);      // do not animate the diagram
        lineChart2.setLegendVisible(false); // do not show the legend

        XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
        for (double x = -4.9;  x < 4.9;  x += 0.1) {
            series2.getData().add(new XYChart.Data<>(x, Math.sin(x) * 10));
        }
        XYChart.Series<Number, Number> series3 = new XYChart.Series<>();
        for (double x = -4.9;  x < 4.9;  x += 0.1) {
            series3.getData().add(new XYChart.Data<>(x, Math.signum(Math.sin(x + 1.2)) * 5 ));
        }

        VBox charts = new VBox();
        charts.getChildren().addAll(lineChart, lineChart2);
        Scene scene  = new Scene(charts);
        lineChart.getData().add(series);
        lineChart2.getData().add(series2);
        lineChart2.getData().add(series3);
 
        URL styleSheet = getClass().getResource("chartExample.css");
        scene.getStylesheets().add(styleSheet.toExternalForm());

        stage.setScene(scene);
        stage.show();
    }
}
