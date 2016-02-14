package afester.javafx.hexedit;

import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class HexDumpSkin extends SkinBase<HexDump> {

    public HexDumpSkin(HexDump control) {
        super(control); // NOTE: SkinBase only has a Control parameter! ... ,
                        // new SevenSegmentPanelBehavior(control)));

        VBox lines = new VBox();
        Font font = new Font("Courier New", 12);
        String c1Sample = "00000000";
        Text t = new Text(c1Sample);
        int col1 = (int) t.getBoundsInLocal().getWidth();
        int height = (int) t.getBoundsInLocal().getHeight() + 4;
        String c2Sample = "12 34 56 78 9A BC DE EF  98 76 54 32 1A BC DE F0";
        t = new Text(c2Sample);
        int col2 = (int) t.getBoundsInLocal().getWidth();
        String c3Sample = "abcdefghijklmnop";
        t = new Text(c3Sample);
        int col3 = (int) t.getBoundsInLocal().getWidth();

        for (int i = 0; i < 10; i++) {
            HBox line = new HBox();

            StackPane cell1 = new StackPane();
            Text t1 = new Text(c1Sample);
            t1.setFont(font);
            Rectangle r1 = new Rectangle(0, 0,  col1, height);
            cell1.getChildren().add(r1);
            cell1.getChildren().add(t1);

            StackPane cell2 = new StackPane();
            Text t2 = new Text(c2Sample);
            t2.setFont(font);
            Rectangle r2 = new Rectangle(0, 0, col2, height);
            cell2.getChildren().add(r2);
            cell2.getChildren().add(t2);

            StackPane cell3 = new StackPane();
            Text t3 = new Text(c3Sample);
            t3.setFont(font);
            Rectangle r3 = new Rectangle(0, 0, col3, height);
            cell3.getChildren().add(r3);
            cell3.getChildren().add(t3);

            //if ( (i % 2) == 0) {
            //    r1.setFill(Color.BLUE);
            //    r2.setFill(Color.GREEN);
            //    r3.setFill(Color.GRAY);
            //} else {
                r1.setFill(Color.LIGHTBLUE);
                r2.setFill(Color.LIGHTGREEN);
                r3.setFill(Color.LIGHTGREY);
            //}
            line.getChildren().add(cell1);
            line.getChildren().add(cell2);
            line.getChildren().add(cell3);
            lines.getChildren().add(line);
        }

        getChildren().add(lines); // !!!! Nodes are added to the Control's
                                  // children through this getChildren()
                                  // method inherited from SkinBase!
    }
}
