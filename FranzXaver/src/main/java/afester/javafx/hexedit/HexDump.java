package afester.javafx.hexedit;


import java.io.InputStream;

import javafx.scene.control.Control;

public class HexDump extends Control {

	public HexDump(InputStream sampleFile) {
		
	}

	/** Skinning and style sheet support */

    @Override
    protected javafx.scene.control.Skin<?> createDefaultSkin() {
        // The skin is the actual content of the control.
        // In the most simple case, the skin is made up of one top level node
        // which is connected to the control here.
        HexDumpSkin skin =  new HexDumpSkin(this);
        return skin;
    };

}
