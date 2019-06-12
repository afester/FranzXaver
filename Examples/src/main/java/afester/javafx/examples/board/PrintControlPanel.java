package afester.javafx.examples.board;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.scene.Group;


public class PrintControlPanel extends Group implements Initializable {

    @FXML
    private ComboBoxFormatable<Printer> printerList;

    @FXML
    private ComboBoxFormatable<Paper> paperSizes;

    public ReadOnlyObjectProperty<Printer> selectedPrinterProperty() {
        return printerList.getSelectionModel().selectedItemProperty();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        printerList.mapText(printer -> printer.getName());
        final double f = 1.0/72 * 25.4;
        paperSizes.mapText(paper -> String.format("%s (%s x %s mm)", 
                                                   paper.getName(), 
                                                   Math.round(paper.getWidth() * f), 
                                                   Math.round(paper.getHeight() * f)));

        // populate the list of printers
        printerList.getItems().addAll(Printer.getAllPrinters());

        selectedPrinterProperty().addListener((obj, oldValue, printer) -> {
            System.err.println("Update printer specific properties");

            // populate the available paper sizes
            printer.getPrinterAttributes().getSupportedPapers().forEach(paper -> paperSizes.getItems().add(paper));
        });

        // printerList.getSelectionModel().select(0);
        // paperSizes.getSelectionModel().select(0);
        printerList.getSelectionModel().select(2); // DEBUG
        paperSizes.getSelectionModel().select(1); // DEBUG
    }

    @FXML
    private void handlePrintAction(ActionEvent event) {
        System.out.println("Printing ..." + printerList);
    }

}
