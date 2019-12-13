package afester.javafx.examples.board;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import afester.javafx.components.DrawingArea;
import afester.javafx.examples.board.model.Board;
import afester.javafx.examples.board.view.BoardView;
import afester.javafx.examples.board.view.BottomBoardView;
import afester.javafx.examples.board.view.TopBoardView;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;


/**
 * PrintPanel
 * +--- Right: PrintPanel.fxml
 * +--- Center: DrawingArea printDrawingView
 *       + transformationPane (???)
 *          + Group paper (from DrawingArea)
 *             + Pane pageView (Size of paper, e.g. DIN A4)
 *                + GridPane printContents  Set to the size of printable area 
 *                   |                      Children clipped to printable size
 *                   |                      Specifies the overall layout of the page
 *                   |
 *                   + 0,0 topLabel
 *                   + 1,0 bottomLabel
 *                   + Line separator
 *                   + 0,1 Group
 *                   |  + BoardView(Pane) topView
 *                   + 1,1 Group
 *                   |   + BoardView(Pane) bottomView
 *                   + 0,2 HBox footer
 */
public class PrintPanel extends BorderPane {

    final Board board;

    // Using an intermediate Group for panning and scaling of the print preview
    // By sending the *Pane* to the printer the panning and scaling of the preview
    // is ignored!

    private final Stage stage;
    private PrintPanelController controller;

    private DrawingArea printDrawingView;

    // The whole page
    private final Pane pageView = new Pane();

    // The printable area
    private PageLayout layout;
    private GridPane printContents; 

    private BoardView topView;
    private BoardView bottomView;
    private Text leftText;
    private Text rightText;

    public PrintPanel(Board b, Stage stage) {
        this.board = b;
        this.stage = stage;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PrintPanel.fxml"));
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

        topView = new TopBoardView(board);
        topView.setReadOnly(true);

        bottomView = new BottomBoardView(board);
        bottomView.setReadOnly(true);
        bottomView.getTransforms().add(Transform.scale(-1, 1));

        leftText = new Text("Top view");
        leftText.setFont(Font.font("Arial", 12));

        rightText = new Text("Bottom view");
        rightText.setFont(Font.font("Arial", 12));

        pageView.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0))));
        pageView.setEffect(new DropShadow(2d, 5, 5, Color.GRAY));

        // HatchFill.createDiagonalHatch(pageView, paperWidth, paperHeight, 2, Color.LIGHTGRAY, 0.3);

        printDrawingView = new DrawingArea();
        printDrawingView.getPaper().getChildren().add(pageView);
        setCenter(printDrawingView);

        setupPage(controller.selectedPrinterProperty().get(), 
                  controller.selectedPaperProperty().get(), 
                  controller.getOrientation()); 
    }


    private void doPrint() {
        Printer printer = controller.selectedPrinterProperty().get();   // TODO: should this be stored in this class?
        PrinterJob job = PrinterJob.createPrinterJob(printer);
        if (job == null) {
            System.err.println("Could not create Printer job!");
            return;
        }

        // Note: by definition, in this application, one unit in the pageView is 1 mm.
        // Since this also applies to the preview page, everything fits in the preview 
        // (the size of the preview pane is directly derived from the width and height, 
        // in mm, of the page layout).

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

        boolean printed = job.printPage(layout, printContents); // pageView);
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

        final double scale = 72.0/25.4; // factor: mm to points

        // setup the page to the current size and remove any child nodes
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
        pageView.setMinSize(paperWidth * scale, paperHeight * scale);
        pageView.getChildren().clear();

        // Create a print layout to determine the margins and the printable size
        layout = printer.createPageLayout(paper, orientation, MarginType.DEFAULT); // .HARDWARE_MINIMUM);
        final double leftMargin = pt2mm(layout.getLeftMargin());
        final double topMargin = pt2mm(layout.getTopMargin());
        final double rightMargin = pt2mm(layout.getRightMargin());
        final double bottomMargin = pt2mm(layout.getBottomMargin());
        final double printableWidth = pt2mm(layout.getPrintableWidth());
        final double printableHeight = pt2mm(layout.getPrintableHeight());
        System.err.println("Layout : " + layout);
        System.err.printf("Margin: %s %s %s %s (%s x %s)\n", leftMargin, topMargin, rightMargin, bottomMargin, 
                printableWidth, printableHeight);

//------------ Main Layout - the printable area
        
        var printableArea = new StackPane();
        printableArea.setMinSize(printableWidth * scale, printableHeight * scale);
        printableArea.setMaxSize(printableWidth * scale, printableHeight * scale);
        printableArea.setLayoutX(leftMargin * scale);
        printableArea.setLayoutY(topMargin * scale);
        printableArea.setClip(new Rectangle(0, 0, printableWidth * scale, printableHeight * scale));
//        printableArea.setBackground(
//                new Background(
//                        new BackgroundFill(Color.CYAN, CornerRadii.EMPTY, Insets.EMPTY)));

        printContents = new GridPane();
        // printContents.setGridLinesVisible(true);
//        printContents.setBackground(
//            new Background(
//                new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));


        // Set up the main layout
        //    HGrow=ALWAYS/50%      HGrow=ALWAYS/50%
        // +--------------------+--------------------+
        // |                    |                    | VGrow=NEVER
        // +--------------------+--------------------+
        // |                    |                    |
        // |                    |                    | VGrow=ALWAYS
        // |                    |                    |
        // +--------------------+--------------------+
        // |                                         | VGrow=NEVER
        // +-----------------------------------------+
        final var cc1 = new ColumnConstraints();
        cc1.setPercentWidth(50);
        cc1.setHgrow(Priority.ALWAYS);
        cc1.setHalignment(HPos.CENTER);
        final var cc2 = new ColumnConstraints();
        cc2.setPercentWidth(50);
        cc2.setHgrow(Priority.ALWAYS);
        cc2.setHalignment(HPos.CENTER);
        printContents.getColumnConstraints().addAll(cc1, cc2);

        final var rc1 = new RowConstraints();
        rc1.setVgrow(Priority.NEVER);
        final var rc2 = new RowConstraints();
        rc2.setVgrow(Priority.ALWAYS);
        final var rc3 = new RowConstraints();
        rc3.setVgrow(Priority.NEVER);
        printContents.getRowConstraints().addAll(rc1, rc2, rc3);

//--------------- Footer
        final var footer = new HBox();
        footer.setPadding(new Insets(1));
        // footer.setBackground(new Background(new BackgroundFill(Color.LIGHTYELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
        footer.setStyle("-fx-border-color: black; -fx-border-style: solid outside none none none; -fx-border-width: 0.4px 0 0 0");
        footer.setSpacing(5);   // spacing between left and right column

        final var leftColumn = new GridPane();
        leftColumn.setHgap(1);
        // leftColumn.setGridLinesVisible(true);
        leftColumn.getChildren().addAll(
                new GridText("Board:",                 0, 0, Color.GRAY),
                new GridText(board.getFileName(),      1, 0),
                new GridText("Schematic:",             0, 1, Color.GRAY),
                new GridText(board.getSchematicFile(), 1, 1));

        final var rightColumn = new GridPane();
        rightColumn.setHgap(1);
        //rightColumn.setGridLinesVisible(true);
        final var now = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
                                         .format(LocalDateTime.now());
        rightColumn.getChildren().addAll(
                new GridText("Date:", 0, 0, Color.GRAY),
                new GridText(now,     1, 0));

        footer.getChildren().addAll(leftColumn, rightColumn);
        footer.setPrefWidth(printableWidth);

//--------------- Assemble page
        // a border around the printable area can be specified like this:
        // printContents.setStyle("-fx-border-color: red; -fx-border-style: solid; -fx-border-width: 0.4px");

        // the board views need to be put into a group since they are not managed
        // and also the bottom view is transformed to be mirrored. By wrapping them
        // in a group the coordinate system is normalized again.

        final var abc = new Group(topView);
        abc.setScaleX(scale);
        abc.setScaleY(scale);
        final var topGroup = new StackPane(abc);
        topGroup.setStyle("-fx-border-style: segments(3, 3), solid;  -fx-border-color: blue, red; -fx-border-width: 0.4px, 1.0px");
        // topGroup.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));

        final var xyz = new Group(bottomView);
        xyz.setScaleX(scale);
        xyz.setScaleY(scale);
        final var bottomGroup = new StackPane(xyz);
        // bottomGroup.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));

        final var leftHeader = new StackPane(leftText);
        leftHeader.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        final var rightHeader = new StackPane(rightText);
        rightHeader.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        printContents.add(leftHeader,  0, 0);
        printContents.add(rightHeader, 1, 0);
        printContents.add(topGroup,    0, 1);
        printContents.add(bottomGroup, 1, 1);
        printContents.add(footer,      0, 2, 2, 1);
        
        printableArea.getChildren().add(printContents);
        pageView.getChildren().add(printableArea); // printContents);

        stage.sizeToScene();    // required to properly fit the content to the window
        // getDrawingView().fitContentToWindow();
        printDrawingView.centerContent();
    }

    // NOTE: 1.0/73.2 was required in order to print the complete border.
    // With the initial 1.0/72.0, the border was clipped left and bottom.
    // This is reproduceable with the Microsoft print to PDF printer.
    // Also, there seems to be an offset of 1mm or so with the printable area
    // moved to the left.
    //private final static double PT2MM = 1.0/72.0 * 25.4;
    private final static double PT2MM = 1.0/73.2 * 25.4;

    private double pt2mm(double points) {
        return points * PT2MM;
    }

    public DrawingArea getDrawingArea() {
        return printDrawingView;
    }
}
