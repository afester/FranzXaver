/*
 * Created 2014 by Tomas Mikula.
 *
 * The author dedicates this file to the public domain.
 */

package afester.javafx.examples.docbook;

import static org.fxmisc.richtext.model.TwoDimensional.Bias.Backward;
import static org.fxmisc.richtext.model.TwoDimensional.Bias.Forward;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.StyledTextArea;
import org.fxmisc.richtext.TextExt;
import org.fxmisc.richtext.model.Codec;
import org.fxmisc.richtext.model.ListItem;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.ReadOnlyStyledDocument;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyledDocument;
import org.fxmisc.richtext.model.StyledText;
import org.fxmisc.richtext.model.TextOps;
import org.reactfx.SuspendableNo;
import org.reactfx.util.Either;
import org.reactfx.util.Tuple2;

import afester.javafx.examples.docbook.CustomObject.CustomObjectOps;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class RichText extends Application {

    public static void main(String[] args) {
        // The following properties are required on Linux for improved text rendering
        //System.setProperty("prism.lcdtext", "false");
        //System.setProperty("prism.text", "t2k");
        launch(args);
    }

    private final TextOps<StyledText<TextStyle>, TextStyle> styledTextOps = StyledText.textOps(); // .textOps(TextStyle.EMPTY);
    private final CustomObjectOps<TextStyle> linkedImageOps = new CustomObjectOps<>(TextStyle.EMPTY);

    private final GenericStyledArea<ParStyle, Either<StyledText<TextStyle>, CustomObject<TextStyle>>, TextStyle> area =
            new GenericStyledArea<>(
                    ParStyle.EMPTY,                                                 // default paragraph style
                    (paragraph, style) -> paragraph.getStyleClass().addAll(style.getStyles()), //  setStyle(style.toCss()),        // paragraph style setter

                    TextStyle.EMPTY,                                                // default segment style
                    styledTextOps._or(linkedImageOps),                              // segment operations
                    seg -> createNode(seg,
                                      (text, style) -> text.getStyleClass().addAll(style.getStyles()) )); // Node creator and segment style setter
    {
        area.setWrapText(true);
        area.setStyleCodecs(
                ParStyle.CODEC,
                Codec.eitherCodec(StyledText.codec(TextStyle.CODEC), LinkedImage.codec(TextStyle.CODEC)));
    }

    private Stage mainStage;
    private TextArea structureView;

    private final SuspendableNo updatingToolbar = new SuspendableNo();

    @Override
    public void start(Stage primaryStage) {
        mainStage = primaryStage;

        structureView = new TextArea();
        Parent editor = createEditorPanel();

        Tab tab = new Tab();
        tab.setText("Edit");
        tab.setClosable(false);
        tab.setOnSelectionChanged(e -> { area.requestFocus(); } );
        tab.setContent(editor);

        Tab tab2 = new Tab();
        tab2.setText("View Structure");
        tab2.setClosable(false);
        tab2.setOnSelectionChanged(e -> refreshStructureView(e));
        tab2.setContent(structureView);

        Tab tab3 = new Tab();
        tab3.setText("View XML");
        tab3.setClosable(false);

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(tab, tab2, tab3);

        Scene scene = new Scene(tabPane, 1024, 600);
        scene.getStylesheets().add(RichText.class.getResource("rich-text.css").toExternalForm());
        primaryStage.setScene(scene);
        area.requestFocus();
        primaryStage.setTitle("Rich Text Demo");
        primaryStage.show();
        
        loadXML(new File("data/Sample%20Page.xml"));
    }


    private void refreshStructureView(Event e) {
        structureView.clear();

        structureView.appendText("StyledDocument\n");
        for (Paragraph<ParStyle, Either<StyledText<TextStyle>,CustomObject<TextStyle>>, TextStyle> p : area.getDocument().getParagraphs()) {
            structureView.appendText("   Paragraph[" + p.getParagraphStyle() + "]\n");
            for (Either<StyledText<TextStyle>, CustomObject<TextStyle>> s : p.getSegments()) {
                if (s.isLeft()) {
                    structureView.appendText("      " + s.getLeft() + "\n");    
                } else {
                    structureView.appendText("      " + s.getRight() + "\n");
                }
            }
        }
    }


    private Parent createEditorPanel() {
        Button loadBtn = createButton("loadfile", this::loadDocument, "Load document");
        Button saveBtn = createButton("savefile", this::saveDocument, "Save document");
        Button nonPrintableBtn = createButton("viewnonprintable", this::saveDocument, "View non printable characters");
        Button undoBtn = createButton("undo", area::undo, "Undo last action");
        Button redoBtn = createButton("redo", area::redo, "Redo last undone action");
        Button backBtn = createButton("back", area::redo, "");
        Button forwardBtn = createButton("forward", area::redo, "");
        Button insertimageBtn = createButton("insertimage", area::redo, "");
        Button insertformulaBtn = createButton("insertformula", area::redo, "");
        Button searchBtn = createButton("search", area::redo, "Search");
        Button cutBtn = createButton("cut", area::cut, "Cut");
        Button copyBtn = createButton("copy", area::copy, "Copy");
        Button pasteBtn = createButton("paste", area::paste, "Paste");

        Button keywordBtn = createButton("keyword",     this::toggleKeyword,   "Format as Keyword");
        Button weblinkBtn = createButton("weblink",     this::toggleWeblink,   "Format as web link");
        Button emphasizeBtn = createButton("emphasize", this::toggleEmphasize, "Format as emphasized");
        Button highlightBtn = createButton("highlight", this::toggleHighlight, "Format as highlighted");
        Button codeBtn = createButton("code",           this::toggleCode,      "Format as Code");

        IconDropDown formatparagraphBtn = new IconDropDown("formatparagraph");
        formatparagraphBtn.addItem("Standard");
        formatparagraphBtn.addItem("Tip");
        formatparagraphBtn.addItem("Warning");
        formatparagraphBtn.addItem("Quotation");
        IconDropDown formatheaderBtn = new IconDropDown("formatheader");
        formatheaderBtn.addItem("Title 1");
        formatheaderBtn.addItem("Title 2");
        formatheaderBtn.addItem("Title 3");
        IconDropDown formatlistBtn = new IconDropDown("formatlist");
        formatlistBtn.addItem("Unordered List");
        formatlistBtn.addItem("Ordered List");
        IconDropDown formatcodeBtn = new IconDropDown("formatcode");
        formatcodeBtn.addItem("C++");
        formatcodeBtn.addItem("Java");
        formatcodeBtn.addItem("Python");
        formatcodeBtn.addItem("SQL");
        formatcodeBtn.addItem("XML");
        formatcodeBtn.addItem("Bash");
        formatcodeBtn.addItem("Generic");

        Button indentlessBtn = createButton("indentless", this::indentLess, "");
        Button indentmoreBtn = createButton("indentmore", this::indentMore, "");

//        ToggleGroup alignmentGrp = new ToggleGroup();
//        ToggleButton alignLeftBtn = createToggleButton(alignmentGrp, "align-left", this::alignLeft);
//        ToggleButton alignCenterBtn = createToggleButton(alignmentGrp, "align-center", this::alignCenter);
//        ToggleButton alignRightBtn = createToggleButton(alignmentGrp, "align-right", this::alignRight);
//        ToggleButton alignJustifyBtn = createToggleButton(alignmentGrp, "align-justify", this::alignJustify);
//        ColorPicker paragraphBackgroundPicker = new ColorPicker();
//        ComboBox<Integer> sizeCombo = new ComboBox<>(FXCollections.observableArrayList(5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16, 18, 20, 22, 24, 28, 32, 36, 40, 48, 56, 64, 72));
//        sizeCombo.getSelectionModel().select(Integer.valueOf(12));
//        ComboBox<String> familyCombo = new ComboBox<>(FXCollections.observableList(Font.getFamilies()));
//        familyCombo.getSelectionModel().select("Serif");
//        ColorPicker textColorPicker = new ColorPicker(Color.BLACK);
//        ColorPicker backgroundColorPicker = new ColorPicker();
//
//        paragraphBackgroundPicker.setTooltip(new Tooltip("NonEmptyParagraph background"));
//        textColorPicker.setTooltip(new Tooltip("Text color"));
//        backgroundColorPicker.setTooltip(new Tooltip("Text background"));
//
//        paragraphBackgroundPicker.valueProperty().addListener((o, old, color) -> updateParagraphBackground(color));
//        sizeCombo.setOnAction(evt -> updateFontSize(sizeCombo.getValue()));
//        familyCombo.setOnAction(evt -> updateFontFamily(familyCombo.getValue()));
//        textColorPicker.valueProperty().addListener((o, old, color) -> updateTextColor(color));
//        backgroundColorPicker.valueProperty().addListener((o, old, color) -> updateBackgroundColor(color));

        undoBtn.disableProperty().bind(Bindings.not(area.undoAvailableProperty()));
        redoBtn.disableProperty().bind(Bindings.not(area.redoAvailableProperty()));

        BooleanBinding selectionEmpty = new BooleanBinding() {
            { bind(area.selectionProperty()); }

            @Override
            protected boolean computeValue() {
                return area.getSelection().getLength() == 0;
            }
        };

        cutBtn.disableProperty().bind(selectionEmpty);
        copyBtn.disableProperty().bind(selectionEmpty);

        area.beingUpdatedProperty().addListener((o, old, beingUpdated) -> {
            if(!beingUpdated) {
                boolean bold, italic, underline, strike;
                Integer fontSize;
                String fontFamily;
                Color textColor;
                Color backgroundColor;

                IndexRange selection = area.getSelection();
                if(selection.getLength() != 0) {
                    StyleSpans<TextStyle> styles = area.getStyleSpans(selection);
//
//                    bold = styles.styleStream().anyMatch(s -> s.bold.orElse(false));
//                    italic = styles.styleStream().anyMatch(s -> s.italic.orElse(false));
//                    underline = styles.styleStream().anyMatch(s -> s.underline.orElse(false));
//                    strike = styles.styleStream().anyMatch(s -> s.strikethrough.orElse(false));
//                    int[] sizes = styles.styleStream().mapToInt(s -> s.fontSize.orElse(-1)).distinct().toArray();
//                    fontSize = sizes.length == 1 ? sizes[0] : -1;
//                    String[] families = styles.styleStream().map(s -> s.fontFamily.orElse(null)).distinct().toArray(String[]::new);
//                    fontFamily = families.length == 1 ? families[0] : null;
//                    Color[] colors = styles.styleStream().map(s -> s.textColor.orElse(null)).distinct().toArray(Color[]::new);
//                    textColor = colors.length == 1 ? colors[0] : null;
//                    Color[] backgrounds = styles.styleStream().map(s -> s.backgroundColor.orElse(null)).distinct().toArray(i -> new Color[i]);
//                    backgroundColor = backgrounds.length == 1 ? backgrounds[0] : null;
                } else {
                    int p = area.getCurrentParagraph();
                    int col = area.getCaretColumn();
                    TextStyle style = area.getStyleAtPosition(p, col);

//                    bold = style.bold.orElse(false);
//                    italic = style.italic.orElse(false);
//                    underline = style.underline.orElse(false);
//                    strike = style.strikethrough.orElse(false);
//                    fontSize = style.fontSize.orElse(-1);
//                    fontFamily = style.fontFamily.orElse(null);
//                    textColor = style.textColor.orElse(null);
//                    backgroundColor = style.backgroundColor.orElse(null);
                }

                int startPar = area.offsetToPosition(selection.getStart(), Forward).getMajor();
                int endPar = area.offsetToPosition(selection.getEnd(), Backward).getMajor();
                List<Paragraph<ParStyle, Either<StyledText<TextStyle>,CustomObject<TextStyle>>, TextStyle>> pars = area.getParagraphs().subList(startPar, endPar + 1);

//                @SuppressWarnings("unchecked")
//                Optional<TextAlignment>[] alignments = pars.stream().map(p -> p.getParagraphStyle().alignment).distinct().toArray(Optional[]::new);
//                Optional<TextAlignment> alignment = alignments.length == 1 ? alignments[0] : Optional.empty();
//
//                @SuppressWarnings("unchecked")
//                Optional<Color>[] paragraphBackgrounds = pars.stream().map(p -> p.getParagraphStyle().backgroundColor).distinct().toArray(Optional[]::new);
//                Optional<Color> paragraphBackground = paragraphBackgrounds.length == 1 ? paragraphBackgrounds[0] : Optional.empty();

                updatingToolbar.suspendWhile(() -> {
//                    if(bold) {
//                        if(!boldBtn.getStyleClass().contains("pressed")) {
//                            boldBtn.getStyleClass().add("pressed");
//                        }
//                    } else {
//                        boldBtn.getStyleClass().remove("pressed");
//                    }
//
//                    if(italic) {
//                        if(!italicBtn.getStyleClass().contains("pressed")) {
//                            italicBtn.getStyleClass().add("pressed");
//                        }
//                    } else {
//                        italicBtn.getStyleClass().remove("pressed");
//                    }
//
//                    if(underline) {
//                        if(!underlineBtn.getStyleClass().contains("pressed")) {
//                            underlineBtn.getStyleClass().add("pressed");
//                        }
//                    } else {
//                        underlineBtn.getStyleClass().remove("pressed");
//                    }
//
//                    if(strike) {
//                        if(!strikeBtn.getStyleClass().contains("pressed")) {
//                            strikeBtn.getStyleClass().add("pressed");
//                        }
//                    } else {
//                        strikeBtn.getStyleClass().remove("pressed");
//                    }
//
//                    if(alignment.isPresent()) {
//                        TextAlignment al = alignment.get();
//                        switch(al) {
//                            case LEFT: alignmentGrp.selectToggle(alignLeftBtn); break;
//                            case CENTER: alignmentGrp.selectToggle(alignCenterBtn); break;
//                            case RIGHT: alignmentGrp.selectToggle(alignRightBtn); break;
//                            case JUSTIFY: alignmentGrp.selectToggle(alignJustifyBtn); break;
//                        }
//                    } else {
//                        alignmentGrp.selectToggle(null);
//                    }
//
//                    paragraphBackgroundPicker.setValue(paragraphBackground.orElse(null));
//
//                    if(fontSize != -1) {
//                        sizeCombo.getSelectionModel().select(fontSize);
//                    } else {
//                        sizeCombo.getSelectionModel().clearSelection();
//                    }
//
//                    if(fontFamily != null) {
//                        familyCombo.getSelectionModel().select(fontFamily);
//                    } else {
//                        familyCombo.getSelectionModel().clearSelection();
//                    }
//
//                    if(textColor != null) {
//                        textColorPicker.setValue(textColor);
//                    }
//
//                    backgroundColorPicker.setValue(backgroundColor);
                });
            }
        });

        HBox toolBar = new HBox();
        toolBar.setSpacing(5);                      // space between each of the TitledToolbars

        TitledToolbar actionToolbar = new TitledToolbar("Actions");
        actionToolbar.addButtons(loadBtn, saveBtn, nonPrintableBtn, undoBtn, redoBtn, backBtn, 
                                 forwardBtn, insertimageBtn, insertformulaBtn, searchBtn, cutBtn, copyBtn, pasteBtn);
        HBox.setHgrow(actionToolbar, Priority.ALWAYS);

        TitledToolbar textStyleToolbar = new TitledToolbar("Text style");
        textStyleToolbar.addButtons(keywordBtn, weblinkBtn, emphasizeBtn, highlightBtn, codeBtn);
        HBox.setHgrow(textStyleToolbar, Priority.ALWAYS);

        TitledToolbar blockStyleToolbar = new TitledToolbar("Block style");
        blockStyleToolbar.addButtons(formatparagraphBtn,formatheaderBtn,formatlistBtn,formatcodeBtn,indentlessBtn,indentmoreBtn);
        HBox.setHgrow(blockStyleToolbar, Priority.ALWAYS);

        toolBar.getChildren().addAll(actionToolbar, textStyleToolbar, blockStyleToolbar);


        VirtualizedScrollPane<GenericStyledArea<ParStyle, Either<StyledText<TextStyle>,CustomObject<TextStyle>>, TextStyle>> vsPane = 
                new VirtualizedScrollPane<>(area);
        VBox.setVgrow(vsPane, Priority.ALWAYS);

        VBox editorPanel = new VBox();
        editorPanel.setPadding(new Insets(5));  // distance to children - space around the toolbox area and the editor pane
        editorPanel.setSpacing(5);              // space between toolbar area and editor pane

        // vsPane.setPadding(new Insets(5)); // does not work - need to add another container around the vsPane
        VBox c1 = new VBox();
        VBox.setVgrow(c1, Priority.ALWAYS);
        c1.getChildren().add(vsPane);
        c1.getStyleClass().add("editorContainer");

        editorPanel.getChildren().addAll(toolBar, c1);

        return editorPanel;
    }


    private Node createNode(Either<StyledText<TextStyle>, CustomObject<TextStyle>> seg,
                            BiConsumer<? super TextExt, TextStyle> applyStyle) {
        if (seg.isLeft()) {
            return StyledTextArea.createStyledTextNode(seg.getLeft(), styledTextOps, applyStyle);
        } else {
            return seg.getRight().createNode();
        }
    }


    private Button createButton(String styleClass, Runnable action, String toolTip) {
        Button button = new Button();
        button.getStyleClass().add(styleClass);
        button.setOnAction(evt -> {
            action.run();
            area.requestFocus();
        });
        button.setPrefWidth(30);
        button.setPrefHeight(30);
        if (toolTip != null) {
            button.setTooltip(new Tooltip(toolTip));
        }
        return button;
    }

    private ToggleButton createToggleButton(ToggleGroup grp, String styleClass, Runnable action) {
        ToggleButton button = new ToggleButton();
        button.setToggleGroup(grp);
        button.getStyleClass().add(styleClass);
        button.setOnAction(evt -> {
            action.run();
            area.requestFocus();
        });
        button.setPrefWidth(20);
        button.setPrefHeight(20);
        return button;
    }

    
    

    private void toggleKeyword() {
        //updateStyleInSelection("bold", spans -> !spans.styleStream().allMatch(style -> style.contains("bold")));
    }

    private void toggleWeblink() {
        //updateStyleInSelection("italic", spans -> !spans.styleStream().allMatch(style -> style.contains("italic")));
    }

    private void toggleEmphasize() {
//      updateStyleInSelection(spans -> TextStyle.strikethrough(!spans.styleStream().allMatch(style -> style.strikethrough.orElse(false))));
    }

    private void toggleHighlight() {
        updateStyleInSelection("highlight", spans -> !spans.styleStream().allMatch(style -> style.contains("highlight")));
    }

    private void toggleCode() {
//      updateStyleInSelection(spans -> TextStyle.strikethrough(!spans.styleStream().allMatch(style -> style.strikethrough.orElse(false))));
    }

    private void indentLess() {
        int pIdx = area.getCurrentParagraph();
        Paragraph<ParStyle, Either<StyledText<TextStyle>, CustomObject<TextStyle>>, TextStyle> paragraph = area.getParagraph(pIdx);
        Optional<ListItem> li = paragraph.getListItem();

        if (li.isPresent()) {
            ListItem newItem = null;
            int level = li.get().getLevel() - 1;
            if (level != 0) {
                newItem = new ListItem(level);
            }

            area.setParagraphList(pIdx, newItem);
    
            // Force recreation of the ParagraphBox ... (TODO)
            updateParagraphStyleInSelection(ParStyle.EMPTY); 
            // updateParagraphStyleInSelection(ParStyle.backgroundColor(Color.WHITE));
        }

    }

    private void indentMore() {
        System.err.println("INDENT MORE");

        int pIdx = area.getCurrentParagraph();
        Paragraph<ParStyle, Either<StyledText<TextStyle>, CustomObject<TextStyle>>, TextStyle> paragraph = area.getParagraph(pIdx);
        Optional<ListItem> li = paragraph.getListItem();

        int level = 1;
        if (li.isPresent()) {
            level = li.get().getLevel() + 1;
        }
        area.setParagraphList(pIdx, new ListItem(level));

        // Force recreation of the ParagraphBox ...
        updateParagraphStyleInSelection(ParStyle.EMPTY.updateWith("bold", true)); //  backgroundColor(Color.YELLOW));
        updateParagraphStyleInSelection(ParStyle.EMPTY.updateWith("bold", false)); //  backgroundColor(Color.YELLOW)););
    }


    private void loadDocument() {
        String initialDir = System.getProperty("user.dir");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load document");
        fileChooser.setInitialDirectory(new File(initialDir));
        File selectedFile = fileChooser.showOpenDialog(mainStage);
        if (selectedFile != null) {
            area.clear();
            //load(selectedFile);
            loadXML(selectedFile);
        }
    }

    private void load(File file) {
        if(area.getStyleCodecs().isPresent()) {
            Tuple2<Codec<ParStyle>, Codec<Either<StyledText<TextStyle>, CustomObject<TextStyle>>>> codecs = area.getStyleCodecs().get();
            Codec<StyledDocument<ParStyle, Either<StyledText<TextStyle>, CustomObject<TextStyle>>, TextStyle>>
                codec = ReadOnlyStyledDocument.codec(codecs._1, codecs._2, area.getSegOps());

            try {
                FileInputStream fis = new FileInputStream(file);
                DataInputStream dis = new DataInputStream(fis);
                StyledDocument<ParStyle, Either<StyledText<TextStyle>, CustomObject<TextStyle>>, TextStyle> doc = codec.decode(dis);
                fis.close();

                if(doc != null) {
                    area.replaceSelection(doc);
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadXML(File file) {
        DocbookImporter di = new DocbookImporter("", "");
        try (InputStream is = new FileInputStream(file)){
            di.importFromFile(is, new DocbookHandler() {

                @Override
                public void addParagraph(String content, String style) {
                    ParStyle pStyle = ParStyle.EMPTY.updateWith(style, true);
                    TextStyle tStyle = TextStyle.EMPTY.updateWith(style, true);

                    StyledDocument<ParStyle, Either<StyledText<TextStyle>, CustomObject<TextStyle>>, TextStyle> doc = 
                    ReadOnlyStyledDocument.<ParStyle, Either<StyledText<TextStyle>, CustomObject<TextStyle>>, TextStyle>
                                fromString(content + "\n", pStyle, tStyle, styledTextOps._or(linkedImageOps));

                    area.append(doc);
                }

                @Override
                public void addCode(String content, String language) {
                    ParStyle pStyle = ParStyle.EMPTY.updateWith("programlisting", true).updateWith(language, true);
                    TextStyle tStyle = TextStyle.EMPTY.updateWith("programlisting", true).updateWith(language, true);

                    StyledDocument<ParStyle, Either<StyledText<TextStyle>, CustomObject<TextStyle>>, TextStyle> doc = 
                    ReadOnlyStyledDocument.<ParStyle, Either<StyledText<TextStyle>, CustomObject<TextStyle>>, TextStyle>
                                fromString(content + "\n", pStyle, tStyle, styledTextOps._or(linkedImageOps));

                    area.append(doc);
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    private void saveDocument() {
        String initialDir = System.getProperty("user.dir");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save document");
        fileChooser.setInitialDirectory(new File(initialDir));
        File selectedFile = fileChooser.showSaveDialog(mainStage);
        if (selectedFile != null) {
            save(selectedFile);
        }
    }


    private void save(File file) {
        StyledDocument<ParStyle, Either<StyledText<TextStyle>, CustomObject<TextStyle>>, TextStyle> doc = area.getDocument();

        // Use the Codec to save the document in a binary format
        area.getStyleCodecs().ifPresent(codecs -> {
            Codec<StyledDocument<ParStyle, Either<StyledText<TextStyle>, CustomObject<TextStyle>>, TextStyle>> codec =
                    ReadOnlyStyledDocument.codec(codecs._1, codecs._2, area.getSegOps());
            try {
                FileOutputStream fos = new FileOutputStream(file);
                DataOutputStream dos = new DataOutputStream(fos);
                codec.encode(dos, doc);
                fos.close();
            } catch (IOException fnfe) {
                fnfe.printStackTrace();
            }
        });
    }


    /**
     * Action listener which inserts a new image at the current caret position.
     */
    private void insertImage() {
        String initialDir = System.getProperty("user.dir");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Insert image");
        fileChooser.setInitialDirectory(new File(initialDir));
        File selectedFile = fileChooser.showOpenDialog(mainStage);
        if (selectedFile != null) {
            String imagePath = selectedFile.getAbsolutePath();
            imagePath = imagePath.replace('\\',  '/');
            ReadOnlyStyledDocument<ParStyle, Either<StyledText<TextStyle>, CustomObject<TextStyle>>, TextStyle> ros =
                    ReadOnlyStyledDocument.fromSegment(Either.right(new LinkedImage<>(imagePath, TextStyle.EMPTY)),
                                                       ParStyle.EMPTY, TextStyle.EMPTY, area.getSegOps());
            area.replaceSelection(ros);
        }
    }

    private void updateStyleInSelection(String modStyle, Function<StyleSpans<TextStyle>, Boolean> mixinGetter) {
        IndexRange selection = area.getSelection();
        if(selection.getLength() != 0) {
            StyleSpans<TextStyle> styles = area.getStyleSpans(selection);
            Boolean mixin = mixinGetter.apply(styles);
            System.err.println("  MIXIN:" + mixin);
            StyleSpans<TextStyle> newStyles = styles.mapStyles(style -> style.updateWith(modStyle, mixin.booleanValue()));
            area.setStyleSpans(selection.getStart(), newStyles);
        }
    }

    private void updateStyleInSelection(TextStyle mixin) {
        IndexRange selection = area.getSelection();
        if (selection.getLength() != 0) {
            StyleSpans<TextStyle> styles = area.getStyleSpans(selection);
//            StyleSpans<TextStyle> newStyles = styles.mapStyles(style -> style.updateWith(mixin));
//            area.setStyleSpans(selection.getStart(), newStyles);
        }
    }

    private void updateParagraphStyleInSelection(Function<ParStyle, ParStyle> updater) {
        IndexRange selection = area.getSelection();
        int startPar = area.offsetToPosition(selection.getStart(), Forward).getMajor();
        int endPar = area.offsetToPosition(selection.getEnd(), Backward).getMajor();
        for(int i = startPar; i <= endPar; ++i) {
            Paragraph<ParStyle, Either<StyledText<TextStyle>,CustomObject<TextStyle>>, TextStyle> paragraph = area.getParagraph(i);
            area.setParagraphStyle(i, updater.apply(paragraph.getParagraphStyle()));
        }
    }

    private void updateParagraphStyleInSelection(ParStyle mixin) {
//        updateParagraphStyleInSelection(style -> style.updateWith(mixin));
    }

    private void updateFontSize(Integer size) {
//        if(!updatingToolbar.get()) {
//            updateStyleInSelection(TextStyle.fontSize(size));
//        }
    }

    private void updateFontFamily(String family) {
//        if(!updatingToolbar.get()) {
//            updateStyleInSelection(TextStyle.fontFamily(family));
//        }
    }

    private void updateTextColor(Color color) {
//        if(!updatingToolbar.get()) {
//            updateStyleInSelection(TextStyle.textColor(color));
//        }
    }

    private void updateBackgroundColor(Color color) {
//        if(!updatingToolbar.get()) {
//            updateStyleInSelection(TextStyle.backgroundColor(color));
//        }
    }

    private void updateParagraphBackground(Color color) {
        if(!updatingToolbar.get()) {
//            updateParagraphStyleInSelection(ParStyle.backgroundColor(color));
        }
    }
}
