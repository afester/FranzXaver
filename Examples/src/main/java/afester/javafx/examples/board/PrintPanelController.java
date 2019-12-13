package afester.javafx.examples.board;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import afester.javafx.components.ComboBoxFormatable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;


public class PrintPanelController implements Initializable {

    @FXML
    private ComboBoxFormatable<Printer> printerList;

    @FXML
    private ComboBoxFormatable<Paper> paperSizes;
    
    @FXML
    private ToggleGroup orientationGroup;

    @FXML
    private RadioButton landscape;

    @FXML
    private RadioButton portrait;

    @FXML
    public Button printButton;

    public ReadOnlyObjectProperty<Printer> selectedPrinterProperty() {
        return printerList.getSelectionModel().selectedItemProperty();
    }
    public void setPrinter(String printerName) {
        if (printerName == null) {
            printerList.getSelectionModel().select(0);
        } else {
            Printer printer = printerMap.get(printerName);
            if (printer != null) {
                printerList.getSelectionModel().select(printer);
            }
        }
    }
    public String getPrinter() {
        return printerList.getSelectionModel().getSelectedItem().getName();
    }

    public ReadOnlyObjectProperty<Paper> selectedPaperProperty() {
        return paperSizes.getSelectionModel().selectedItemProperty();
    }
    public int getPaper() {
        return paperSizes.getSelectionModel().getSelectedIndex();
    }
    public void setPaper(String paperName) {
        if (paperName == null) {
            paperSizes.getSelectionModel().select(0);
        } else {
            Paper paper = paperMap.get(paperName);
            if (paper != null) {
                paperSizes.getSelectionModel().select(paper);
            }
        }
    }

    private ObjectProperty<PageOrientation> orientation = new SimpleObjectProperty<>(PageOrientation.LANDSCAPE);
    public PageOrientation getOrientation() { return orientation.get(); }
    public ObjectProperty<PageOrientation> orientationProperty() {
        return orientation;
    }

    final private Map<String, Printer> printerMap = new HashMap<>();
    final private Map<String, Paper> paperMap = new HashMap<>();

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        printerList.mapText(printer -> printer.getName());
        final double f = 1.0/72 * 25.4;
        paperSizes.mapText(paper -> String.format("%s (%s x %s mm)", 
                                                   paper.getName(), 
                                                   Math.round(paper.getWidth() * f), 
                                                   Math.round(paper.getHeight() * f)));

        // populate the list of printers
        Printer.getAllPrinters().forEach(e -> printerMap.put(e.getName(), e));
        printerList.getItems().addAll(printerMap.values());

        selectedPrinterProperty().addListener((obj, oldValue, printer) -> {
            System.err.println("Update printer specific properties");

            // populate the available paper sizes
            paperSizes.getItems().clear();
            paperMap.clear();
            printer.getPrinterAttributes().getSupportedPapers().forEach(paper -> paperMap.put(paper.getName(), paper));
            paperSizes.getItems().addAll(paperMap.values());
            paperSizes.getSelectionModel().select(0);
        });

        orientationGroup.selectToggle(landscape);
        orientationGroup.selectedToggleProperty().addListener((obj, oldValue, newValue) -> {
            if (newValue == landscape) {
                orientation.set(PageOrientation.LANDSCAPE);
            } else {
                orientation.set(PageOrientation.PORTRAIT);
            }
        });
    }
}
