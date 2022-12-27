package afester.javafx.examples.board;

import java.io.IOException;



import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;


public class AboutDialog extends Dialog<Void> {

    public AboutDialog() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("About.fxml"));

        try {
            DialogPane aboutPanel = loader.load();

            Label appVersionLabel = (Label) aboutPanel.lookup("#appVersion");
            appVersionLabel.setText("Development Version");

            Label javaVersionLabel = (Label) aboutPanel.lookup("#javaVersion");
            javaVersionLabel.setText(String.format("%s %s", System.getProperty("java.vm.name"), System.getProperty("java.runtime.version")));

            Label javaFxVersionLabel = (Label) aboutPanel.lookup("#javaFxVersion");
            javaFxVersionLabel.setText(System.getProperty("javafx.runtime.version"));

            final var log4jVersion = "unknown"; // org.apache.logging.log4j.core.Layout.class.getPackage().getImplementationVersion();
            Label log4jVersionLabel = (Label) aboutPanel.lookup("#log4jVersion");
            log4jVersionLabel.setText(log4jVersion);

            setDialogPane(aboutPanel);
            setTitle("About BreadBoardEditor");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
