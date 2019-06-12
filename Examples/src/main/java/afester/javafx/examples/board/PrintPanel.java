package afester.javafx.examples.board;

import java.io.IOException;

import afester.javafx.examples.board.model.Board;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.print.PageLayout;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class PrintPanel extends BorderPane {

    // Using an intermediate Group for panning and scaling of the print preview
    // By sending the *Pane* to the printer the panning and scaling of the preview
    // is ignored!


    // The whole page
    private final Pane pageView = new Pane();

    private DrawingView printDrawingView;
    
    private PrintControlPanel controller;

    public PrintPanel(Board b) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PrintControlPanel.fxml"));
            Parent root = loader.load();
            setRight(root);    

            controller = loader.getController();
            controller.selectedPrinterProperty().addListener((obj, oldValue, newValue)->  {
               System.err.println(newValue);
               setupPage(newValue);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

//        printView.getChildren().clear();

        
//        BoardView bottomView = new BoardView(b);
//
//        final double paperWidth = 297.0;                   // DIN A4 width in landscape format  
//        final double paperHeight = 210.0;                   // DIN A4 width in landscape format
//        final double paperMidpoint = paperWidth / 2;
//        final double boardWidth = b.getWidth();
//        System.err.printf("Paper width: %s\n", paperWidth);
//        System.err.printf("Paper midpoint: %s\n", paperMidpoint);
//        System.err.printf("Board width: %s\n", boardWidth);
//
//        BoardView topView = new BoardView(b);
//        topView.setReadOnly(true);
//
//        bottomView.setReadOnly(true);
//        bottomView.getTransforms().add(Transform.scale(-1, 1));
//        
//        Text topLabel = new Text(paperMidpoint - 10 - boardWidth, 20, "Top view");
//        topLabel.setScaleX(0.6);
//        topLabel.setScaleY(0.6);
//        Text bottomLabel = new Text(paperMidpoint + 10, 20, "Bottom view");
//        bottomLabel.setScaleX(0.6);
//        bottomLabel.setScaleY(0.6);
//
//        // Create paper margin markers
//        Line topMargin =    new Line(0, 10, paperWidth, 10);
//        topMargin.getStrokeDashArray().addAll(2.0, 2.0);
//        topMargin.setStrokeWidth(0.2);
//        Line bottomMargin = new Line(0, paperHeight - 10, paperWidth, paperHeight - 10);
//        bottomMargin.getStrokeDashArray().addAll(2.0, 2.0);
//        bottomMargin.setStrokeWidth(0.2);
//        Line leftMargin =   new Line(10, 0, 10, paperHeight);
//        leftMargin.getStrokeDashArray().addAll(2.0, 2.0);
//        leftMargin.setStrokeWidth(0.2);
//        Line rightMargin =  new Line(paperWidth - 10, 0, paperWidth - 10, paperHeight);
//        rightMargin.getStrokeDashArray().addAll(2.0, 2.0);
//        rightMargin.setStrokeWidth(0.2);
//
//        Group topGroup = new Group(topView);
//        topGroup.setLayoutX(paperMidpoint - 10 - boardWidth);
//        topGroup.setLayoutY(25);
//
//        Group bottomGroup = new Group(bottomView);
//        bottomGroup.setLayoutX(paperMidpoint + 10 + boardWidth);
//        bottomGroup.setLayoutY(25);
//
////        Rectangle r = new Rectangle();
////        r.setX(topView.getLayoutX());
////        r.setY(topView.getLayoutY());
////        r.setWidth(10);
////        r.setHeight(10);
////        r.setStroke(Color.RED);
////        r.setFill(null);
//
//        // The printView is the "Paper" on which we draw.
//        printView.getChildren().addAll(topLabel, bottomLabel, topGroup, bottomGroup,
//                                       topMargin, bottomMargin, leftMargin, rightMargin);
//        printView.setMinSize(paperWidth, paperHeight);
//        printView.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0))));
//        //printView.setGridLinesVisible(true);
//        // printView.getChildren().add(r);
//
        Group panZoomView = new Group(pageView);
        pageView.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0))));
        printDrawingView = new DrawingView(panZoomView);
        setCenter(printDrawingView);

        setupPage(controller.selectedPrinterProperty().get());

//        printTab.setContent(printDrawingView);
//    }
//
//    private void printLayout() {
//        PrinterJob job = PrinterJob.createPrinterJob();
//        job.showPrintDialog(this.stage);
//        
//        // print the board. No scaling and panning of the corresponding DrawingView is considered!
//        // Hence the result is much smaller than expected so we need to scale (and translate) it a bit to
//        // get to the proper actual size.
//        printView.setScaleX(2.5);
//        printView.setScaleY(2.5);
//        printView.setTranslateX(220);
//        printView.setTranslateY(150);
//
//        boolean success = job.printPage(printView);
//        if (success) {
//            job.endJob();
//        }   
//        printView.setScaleX(1.0);
//        printView.setScaleY(1.0);
    }

    private void setupPage(Printer printer) {
        System.err.println("Printer: " + printer); 

        Paper paper = printer.getPrinterAttributes().getDefaultPaper();

        System.err.println(printer.getPrinterAttributes().getDefaultPaper().getHeight());

        pageView.setMinSize(paper.getWidth(), paper.getHeight());

        PageLayout pageLayout = printer.getDefaultPageLayout();
        
        System.err.println(pageLayout.getPrintableHeight());
        System.err.println(pageLayout.getPrintableWidth());
    }

    public DrawingView getDrawingView() {
        return null;
    }


}
