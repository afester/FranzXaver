package afester.javafx.examples.board;

import java.io.IOException;

import afester.javafx.examples.board.model.Board;
import afester.javafx.shapes.Line;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.Printer.MarginType;
import javafx.print.PrinterJob;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

public class PrintPanel extends BorderPane {

    // Using an intermediate Group for panning and scaling of the print preview
    // By sending the *Pane* to the printer the panning and scaling of the preview
    // is ignored!

    private final Stage stage;

    // The whole page
    private final Pane pageView = new Pane();

    private DrawingView printDrawingView;
    private Group panZoomView;

    private PrintControlPanel controller;

    private PageLayout layout;
    private Pane printContents; 

    private BoardView topView;
    private BoardView bottomView;
    private Text topLabel;
    private Text bottomLabel;

    public PrintPanel(Board b, Stage stage) {
        this.stage = stage;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PrintControlPanel.fxml"));
            Parent root = loader.load();
            setRight(root);

            controller = loader.getController();
            controller.selectedPrinterProperty().addListener(
                    (obj, oldValue, newValue) -> 
                        setupPage(newValue, controller.selectedPaperProperty().get(), controller.getOrientation())); 

            controller.selectedPaperProperty().addListener(
                    (obj, oldValue, newValue) ->
                        setupPage(controller.selectedPrinterProperty().get(), newValue, controller.getOrientation()));

            controller.orientationProperty().addListener(
                    (obj, oldValue, newValue) -> {
                        setupPage(controller.selectedPrinterProperty().get(), controller.selectedPaperProperty().get(), newValue);  
            });
            controller.printButton.setOnAction(e->doPrint());
        } catch (IOException e) {
            e.printStackTrace();
        }

        topView = new BoardView(b);
        topView.setReadOnly(true);

        bottomView = new BoardView(b, true);
        bottomView.setReadOnly(true);
        bottomView.getTransforms().add(Transform.scale(-1, 1));

        topLabel = new Text("Top view");
        topLabel.setScaleX(0.6);
        topLabel.setScaleY(0.6);
        bottomLabel = new Text("Bottom view");
        bottomLabel.setScaleX(0.6);
        bottomLabel.setScaleY(0.6);
        panZoomView = new Group(pageView);
        pageView.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0))));
        printDrawingView = new DrawingView(panZoomView);
        printDrawingView.setEffect(new DropShadow(2d, 10, 10, Color.GRAY));
        setCenter(printDrawingView);

        setupPage(controller.selectedPrinterProperty().get(), controller.selectedPaperProperty().get(), controller.getOrientation()); 
    }


    private void doPrint() {
        Printer printer = controller.selectedPrinterProperty().get();   // TODO: should this be stored in this class?
        PrinterJob job = PrinterJob.createPrinterJob(printer);
        if (job == null) {
            System.err.println("Could not create Printer job!");
            return;
        }

        // Issue: by definition, in this application, one unit in the pageView is 1 mm.
        // Since this also applies to the preview page, everything fits in the preview 
        // (the size of the preview pane is directly derived from the width and height, in mm, of the
        // page layout).

        //******************************************************
        // However, the printing unit is "font points". Not dots or the like!
        //
        // > A font is often measured in pt (points). Points dictate the height of the lettering. 
        // > There are approximately 72 (72.272) points in one inch or 2.54 cm. For example, the 
        // > font size 72 would be about one inch tall, and 36 would be about a half of an inch.
        //
        // 1pt = 1/72 inch = 0,3527777... mm (25,4/72)
        // 1mm = 2.83464566929pt      (72/25,4)
        //
        //       2.83687943262 (experimental value)
        // 
        // 1pt = 1/72.272 inch = 0,35145007748 mm (25,4/72.272)
        // 1mm = 2.84535433071pt     (72.272/25,4)

        final double scale = 72.0/25.4; // factor: mm to points

        // ******************************************************

        final double xs = pageView.getScaleX();
        final double ys = pageView.getScaleY();
        pageView.setScaleX(scale);
        pageView.setScaleY(scale);
        pageView.setLayoutX(215);
        pageView.setLayoutY(140);
        
//        final double x = 400;
//        final double y = 200;
//        Pane p = new Pane();
//        p.setBackground(new Background(new BackgroundFill(Color.LIGHTYELLOW, new CornerRadii(0), new Insets(0))));
//        
//        final double screenDPI = Screen.getPrimary().getDpi();
//        System.err.printf("Screen resolution: %s\n", screenDPI);
//
//        final double scaleP = 200.0/70.5;
//        System.err.printf("Print scale: %s\n", scaleP);
//
//        Circle c = new Circle(x, y, 100 * scaleP);
//        //                          ^^^ UNIT? 
//        c.setFill(null);
//        c.setStroke(Color.BLUE);
//        Line l1 = new Line(x-5, y, x+5, y);
//        Line l2 = new Line(x, y - 5, x, y + 5);
//        Circle c2 = new Circle(3, 3, 3);
//        c2.setFill(null);
//        c2.setStroke(Color.RED);
//        
//        final double x2 = 300;
//        final double y2 = 50;
//        Line l21 = new Line(x2-5, y2, x2+5, y2);
//        l21.setStroke(Color.GREEN);
//        Line l22 = new Line(x2, y2 - 5, x2, y2 + 5);
//        l22.setStroke(Color.GREEN);
//
//        final double x3 = 500;
//        final double y3 = 50;
//        Line l31 = new Line(x3-5, y3, x3+5, y3);
//        l31.setStroke(Color.GREEN);
//        Line l32 = new Line(x3, y3 - 5, x3, y3 + 5);
//        l32.setStroke(Color.GREEN);
//        
//        p.getChildren().addAll(c, c2, l1, l2, l21, l22, l31, l32);
//
//        System.err.printf("width: %s, height: %s\n", p.getBoundsInParent().getWidth(), p.getBoundsInParent().getHeight());
//
//        System.err.printf("Job resolution: %s\n", job.getJobSettings().getPrintResolution());

//        double scaleX
//            = layout.getPrintableWidth() / p.getBoundsInParent().getWidth();
//        double scaleY
//            = layout.getPrintableHeight() / p.getBoundsInParent().getHeight();
//        System.err.printf("scaleX: %s, scaleY: %s\n", scaleX, scaleY);
//        p.setScaleX(scaleX);
//        p.setScaleX(scaleY);

        boolean printed = job.printPage(layout, pageView);
        pageView.setScaleX(xs);
        pageView.setScaleY(ys);
        
        if (!printed) {
            System.err.println("Printing failed: " + job); // .getJobStatus());
            return;
        }

        job.endJob();
        System.err.println("Printing Done.");
    }


    private void setupPage(Printer printer, Paper paper, PageOrientation orientation) {
        System.err.println("Printer: " + printer);
        System.err.println("Paper  : " + paper);
        System.err.println("Orienta: " + orientation);

        double paperWidth = 0.0;
        double paperHeight = 0.0;
        if (orientation == PageOrientation.PORTRAIT) {
            paperWidth = paper.getWidth();
            paperHeight = paper.getHeight();
        } else {
            paperWidth = paper.getHeight();
            paperHeight = paper.getWidth();
        }
        paperWidth = pt2mm(paperWidth);
        paperHeight = pt2mm(paperHeight);
        pageView.setMinSize(paperWidth, paperHeight);
        pageView.getChildren().clear();
//        HatchFill.createDiagonalHatch(pageView, paperWidth, paperHeight, 2, Color.DARKGRAY, 0.3);

        layout = printer.createPageLayout(paper, orientation, MarginType.DEFAULT); // MarginType.HARDWARE_MINIMUM);
        System.err.println("Layout : " + layout);
        final double leftMargin = pt2mm(layout.getLeftMargin());
        final double topMargin = pt2mm(layout.getTopMargin());
        final double rightMargin = pt2mm(layout.getRightMargin());
        final double bottomMargin = pt2mm(layout.getBottomMargin());
        final double printableWidth = pt2mm(layout.getPrintableWidth());
        final double printableHeight = pt2mm(layout.getPrintableHeight());
        System.err.printf("Margin: %s %s %s %s (%s x %s)", leftMargin, topMargin, rightMargin, bottomMargin, 
                printableWidth, printableHeight);

        printContents = new Pane();
        printContents.setMinSize(printableWidth, printableHeight);
        printContents.setMaxSize(printableWidth, printableHeight);
        
        // position the left top corner of the print contents so that it is aligned with the printable area
        printContents.setLayoutX(leftMargin);
        printContents.setLayoutY(topMargin);

        printContents.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0))));

        final double contentMidpoint = printableWidth / 2;
        final double boardWidth = topView.getBoard().getWidth();
        Group topGroup = new Group(topView);
        topGroup.setLayoutX(contentMidpoint - 10 - boardWidth);
        topGroup.setLayoutY(20);

        Group bottomGroup = new Group(bottomView);
        bottomGroup.setLayoutX(contentMidpoint + boardWidth + 10);
        bottomGroup.setLayoutY(20);

        topLabel.setX(contentMidpoint - 10 - boardWidth);
        topLabel.setY(10.0);
        bottomLabel.setX(contentMidpoint + 10);
        bottomLabel.setY(10.0);

        Line separator = new Line(contentMidpoint, 0, contentMidpoint, printableHeight);
        separator.getStrokeDashArray().addAll(2.0, 2.0);
        separator.setStroke(Color.BLUE);
        separator.setStrokeWidth(0.3);

//        Rectangle border = new Rectangle(0, 0, printableWidth, printableHeight);
//        border.setFill(null);
//        border.setStroke(Color.BLACK);
//        border.setStrokeWidth(0.4);

        printContents.getChildren().addAll(separator, topLabel, bottomLabel, topGroup, bottomGroup); // , border);
        pageView.getChildren().add(printContents);

        stage.sizeToScene();    // required to properly fit the content to the window
        // getDrawingView().fitContentToWindow();
        printDrawingView.centerContent();
    }

    
    private final static double PT2MM = 1.0/72 * 25.4;

    private double pt2mm(double points) {
        return points * PT2MM;
    }

    public DrawingView getDrawingView() {
        return printDrawingView;
    }

}
