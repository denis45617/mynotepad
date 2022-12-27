package hr.fer.oprpp1.hw08.jnotepadpp;


import hr.fer.oprpp1.custom.collections.LinkedListIndexedCollection;
import hr.fer.oprpp1.custom.collections.List;
import hr.fer.oprpp1.hw08.jnotepadpp.local.*;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.Location;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.MyJTextArea;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.TextEditor;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.TextEditorModel;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.undo.StackState;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.undo.UndoManager;
import hr.fer.oprpp1.hw08.plugins.Plugin;
import hr.fer.oprpp1.hw08.plugins.PluginFactory;


import java.awt.*;
import java.awt.event.*;
import java.io.Serial;
import java.nio.file.Path;
import java.io.File;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.Timer;


/**
 * JNotepadPP class - application class
 */
public class JNotepadPP extends JFrame implements ILocalizationListener {

    @Serial
    private static final long serialVersionUID = 1L;
    private DefaultMultipleDocumentModel multipleDocumentModel;
    private JTabbedPane tabbedPane;
    private JPanel statusBar;
    private final List<JMenuItem> disabledEnabled = new LinkedListIndexedCollection<>();
    private final ILocalizationProvider prov = new FormLocalizationProvider(LocalizationProvider.getInstance(), this);


    /**
     * Public constructor for JNotepadPP
     */
    public JNotepadPP() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                exitAction.actionPerformed(null);
            }
        });

        setLocation(0, 0);
        setSize(600, 600);
        setTitle("(unnamed) - JNotepad++");
        initGUI();
    }


    /**
     * Private function for initializing GUI
     */
    private void initGUI() {
        statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        multipleDocumentModel = new DefaultMultipleDocumentModel();

        multipleDocumentModel.setProv(prov);
        prov.addLocalizationListener(this);
        newTabAction.actionPerformed(null);


        this.getContentPane().setLayout(new BorderLayout());
        tabbedPane = (JTabbedPane) multipleDocumentModel.getVisualComponent();

        multipleDocumentModel.addMultipleDocumentListener(new MultipleDocumentListenerAdapter() {
            @Override
            public void currentDocumentChanged(SingleDocumentModel previousModel, SingleDocumentModel currentModel) {
                Util.setWindowTitle(prov, multipleDocumentModel, JNotepadPP.this);

                //kad se promijeni tab, onda reevaluiraj da li "change case" akcije trebaju biti enabled ili disabled
                TextEditor textEditor = multipleDocumentModel.getCurrentDocument().getTextComponent().getTextEditor();
                boolean hasSelectedText = textEditor.getTextComponent().hasSelectedText();
                JNotepadPP.this.changeEnabledState(hasSelectedText);
                JNotepadPP.this.changeStatusBarValues();

                //ovo je užasno napisano
                cutAction.setEnabled(textEditor.getTextComponent().hasSelectedText());
                copyAction.setEnabled(textEditor.getTextComponent().hasSelectedText());
                pasteAction.setEnabled(!textEditor.getClipboard().isEmpty());
                pasteAndRemoveAction.setEnabled(!textEditor.getClipboard().isEmpty());
                toUpperCaseAction.setEnabled(textEditor.getTextComponent().hasSelectedText());
                toLowerCaseAction.setEnabled(textEditor.getTextComponent().hasSelectedText());
                invertCaseAction.setEnabled(textEditor.getTextComponent().hasSelectedText());
                ascendingAction.setEnabled(textEditor.getTextComponent().hasSelectedText());
                descendingAction.setEnabled(textEditor.getTextComponent().hasSelectedText());
                uniqueAction.setEnabled(textEditor.getTextComponent().hasSelectedText());
            }
        });


        initializeStatusBar();


        this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        this.getContentPane().add(statusBar, BorderLayout.SOUTH);
        createActions();
        createMenus();
        createToolbars();
    }


    /**
     * Action for opening new tab
     */
    private final Action newTabAction = new LocalizableAction("new", "newDesc", prov) {
        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            SingleDocumentModel sdm = multipleDocumentModel.createNewDocument();
            addCaretStatusBarListener(sdm);
            addSomeListeners(sdm);
        }
    };

    private void addCaretStatusBarListener(SingleDocumentModel sdm) {
        sdm.getTextComponent().getTextEditor().getTextComponent().addCursorListener((e1, e2) -> {
            JNotepadPP.this.changeStatusBarValues();
        });
    }

    private void addSomeListeners(SingleDocumentModel sdm){
        TextEditor textEditor = sdm.getTextComponent().getTextEditor();
        UndoManager undoManager = textEditor.getUndoManager();

        //undo  -- listener ovisno o tome ima li čega na undostack
        textEditor.undoAction.setEnabled(false);  //po defaultu je disabled
        undoManager.addUndoStackListener(state -> textEditor.undoAction.setEnabled(state == StackState.NOT_EMPTY));
        //redo -- listener ovisno o tome ima li čega na redo stack
        textEditor.redoAction.setEnabled(false); //po defaultu je disabled
        undoManager.addRedoStackListener(state -> textEditor.redoAction.setEnabled(state == StackState.NOT_EMPTY));

        //cut -- listener ovisno o tome ima li označenog teksta
        textEditor.cutAction.setEnabled(false);  //disabled po defaultu
        //copy -- listener ovisno o tome ima li označenog teksta
        textEditor.copyAction.setEnabled(false);  //disabled po defaultu
        //paste --listener ovisno o tome ima li čega na clipboardstack
        textEditor.pasteAction.setEnabled(false);
        //paste and take --listener ovisno o tome ima li čega na clipboard stack
        textEditor.pasteAndRemoveAction.setEnabled(false);
        //delete selection --listener ovisno o tome ima li označenog teksta
        textEditor.deleteSelectionAction.setEnabled(false);  //disabled po defaultu
        textEditor.getTextComponent().addSelectionListener(() -> textEditor.cutAction.setEnabled(textEditor.getTextComponent().hasSelectedText()));
        textEditor.getTextComponent().addSelectionListener(() -> textEditor.copyAction.setEnabled(textEditor.getTextComponent().hasSelectedText()));
        textEditor.getClipboard().addClipboardListener(() -> textEditor.pasteAction.setEnabled(!textEditor.getClipboard().isEmpty()));
        textEditor.getClipboard().addClipboardListener(() -> textEditor.pasteAndRemoveAction.setEnabled(!textEditor.getClipboard().isEmpty()));
        textEditor.getTextComponent().addSelectionListener(() -> textEditor.deleteSelectionAction.setEnabled(textEditor.getTextComponent().hasSelectedText()));
        textEditor.getTextComponent().addSelectionListener(() -> cutAction.setEnabled(textEditor.getTextComponent().hasSelectedText()));
        textEditor.getTextComponent().addSelectionListener(() -> copyAction.setEnabled(textEditor.getTextComponent().hasSelectedText()));
        textEditor.getClipboard().addClipboardListener(() -> pasteAction.setEnabled(!textEditor.getClipboard().isEmpty()));
        textEditor.getClipboard().addClipboardListener(() -> pasteAndRemoveAction.setEnabled(!textEditor.getClipboard().isEmpty()));
        textEditor.getTextComponent().addSelectionListener(() -> toUpperCaseAction.setEnabled(textEditor.getTextComponent().hasSelectedText()));
        textEditor.getTextComponent().addSelectionListener(() -> toLowerCaseAction.setEnabled(textEditor.getTextComponent().hasSelectedText()));
        textEditor.getTextComponent().addSelectionListener(() -> invertCaseAction.setEnabled(textEditor.getTextComponent().hasSelectedText()));
        textEditor.getTextComponent().addSelectionListener(() -> ascendingAction.setEnabled(textEditor.getTextComponent().hasSelectedText()));
        textEditor.getTextComponent().addSelectionListener(() -> descendingAction.setEnabled(textEditor.getTextComponent().hasSelectedText()));
        textEditor.getTextComponent().addSelectionListener(() -> uniqueAction.setEnabled(textEditor.getTextComponent().hasSelectedText()));
    }


    //OPEEEEEEEEEN DOCCCCCCCUMMMMMMMMMEEEEEEEEENT=======================================================================
    /**
     * Action for opening existing document
     */
    private final Action openDocumentAction = new LocalizableAction("open", "openDesc", prov) {
        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Open file");
            if (fc.showOpenDialog(JNotepadPP.this) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File fileName = fc.getSelectedFile();
            Path filePath = fileName.toPath();

            SingleDocumentModel sdm = multipleDocumentModel.loadDocument(filePath);
            addCaretStatusBarListener(sdm);
            addSomeListeners(sdm);

        }
    };

    //==================================================================================================================
    //SAVE==============================================================================================================
    /**
     * Action for saving document
     */
    private final Action saveDocumentAction = new LocalizableAction("save", "saveDesc", prov) {

        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            SingleDocumentModel sdm = multipleDocumentModel.getCurrentDocument();
            Path openedFilePath = sdm.getFilePath();
            multipleDocumentModel.saveDocument(sdm, openedFilePath);

        }
    };

    //==================================================================================================================
    //SAVE AS===========================================================================================================
    /**
     * Action for saving document as a new file
     */
    private final Action saveDocumentAsAction = new LocalizableAction("saveas", "saveasDesc", prov) {

        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            SingleDocumentModel sdm = multipleDocumentModel.getCurrentDocument();
            multipleDocumentModel.saveDocument(sdm, null);

        }
    };

    //==================================================================================================================
    //CLOSE //closes current tab========================================================================================
    /**
     * Action for closing tab
     */
    private final Action closeAction = new LocalizableAction("close", "closeDesc", prov) {
        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            SingleDocumentModel sdm = multipleDocumentModel.getCurrentDocument();
            fileSaveIfModifiedOnClose(sdm);

            if (multipleDocumentModel.getTabCount() == 1) {
                newTabAction.actionPerformed(null);

            }

            multipleDocumentModel.closeDocument(sdm);
        }
    };

    /**
     * Function that check if document is modified. If it was modifidied it offers saving document before closing it
     *
     * @param sdm document
     * @return status of users decision. Returns done operation 0 = save, 1 = don't save, 2 = cancel
     */
    private int fileSaveIfModifiedOnClose(SingleDocumentModel sdm) {
        if (sdm.isModified()) {
            String ime = (sdm.getFilePath() == null) ? "(unnamed)" : sdm.getFilePath().getFileName().toString();
            String[] options = new String[]{prov.getString("yes"), prov.getString("no"), prov.getString("cancel")};
            int response = JOptionPane.showOptionDialog(null, prov.getString("dywtsf") + ime,
                    prov.getString("save"),
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, options[1]);
            if (response == 0) {
                multipleDocumentModel.saveDocument(sdm, sdm.getFilePath());
            }

            return response;
        }
        return 0;
    }


    //==========================================SELECT ALL==============================================================
    /**
     * Action for selecting all text in the document
     */
    private final Action selectAllAction = new LocalizableAction("selectall", "selectallDesc", prov) {
        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            multipleDocumentModel.getCurrentDocument().getTextComponent().getTextEditor().getTextComponent().selectAll();
        }
    };

    //==================================================================================================================
    //==========================================CUT ====================================================================
    /**
     * Action for cutting selected text
     */
    private final Action cutAction = new LocalizableAction("cut", "cutDesc", prov) {

        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            TextEditor textEditor = multipleDocumentModel.getCurrentDocument().getTextComponent().getTextEditor();
            textEditor.cutAction.actionPerformed(e);
        }
    };

    //==================================================================================================================
    //==========================================COPY ===================================================================
    /**
     * Action for copying selected text
     */
    private final Action copyAction = new LocalizableAction("copy", "copyDesc", prov) {

        @Serial
        private static final long serialVersionUID = 1L;


        @Override
        public void actionPerformed(ActionEvent e) {
            TextEditor textEditor = multipleDocumentModel.getCurrentDocument().getTextComponent().getTextEditor();
            textEditor.copyAction.actionPerformed(e);
        }
    };


    //==================================================================================================================
    //==========================================PASTE===================================================================
    /**
     * Action for pasting selected text
     */
    private final Action pasteAction = new LocalizableAction("paste", "pasteDesc", prov) {

        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            TextEditor textEditor = multipleDocumentModel.getCurrentDocument().getTextComponent().getTextEditor();
            textEditor.pasteAction.actionPerformed(e);
        }
    };


    //==========================================PASTE===================================================================
    /**
     * Action for pasting selected text
     */
    private final Action pasteAndRemoveAction = new LocalizableAction("pasteAndRemove", "pasteAndRemoveDesc", prov) {

        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            TextEditor textEditor = multipleDocumentModel.getCurrentDocument().getTextComponent().getTextEditor();
            textEditor.pasteAndRemoveAction.actionPerformed(e);
        }
    };


    /**
     * Helper function that makes action on selected text.
     *
     * @param caseAction Supported case actions are toUpperCase, toLowerCase, invertCase
     */
    private void caseActions(String caseAction) {
        MyJTextArea editor = multipleDocumentModel.getCurrentDocument().getTextComponent();
        TextEditorModel doc = editor.getTextEditor().getTextComponent();
        if (!doc.hasSelectedText()) return; //ako nema selected text, gtfo

        String text = doc.getTextFromRange(doc.getSelectionRange());
        text = switch (caseAction) {
            case "toUpperCase" -> text.toUpperCase(Locale.ROOT);
            case "toLowerCase" -> text.toLowerCase(Locale.ROOT);
            case "invertCase" -> changeCase(text);
            default -> throw new UnsupportedOperationException();
        };

        doc.deleteRange(doc.getSelectionRange()); //obriši selektirano
        doc.insert(text);

    }

    /**
     * Methods that inverse case of characers in string
     *
     * @param text text
     * @return String with inverted cases (aaAa -> AAaA)
     */
    private String changeCase(String text) {
        char[] znakovi = text.toCharArray();
        for (int i = 0; i < znakovi.length; i++) {
            char c = znakovi[i];
            if (Character.isLowerCase(c)) {
                znakovi[i] = Character.toUpperCase(c);
            } else if (Character.isUpperCase(c)) {
                znakovi[i] = Character.toLowerCase(c);
            }
        }
        return new String(znakovi);
    }


    /**
     * Action for making uppercased text out of selected text
     */
    private final Action toUpperCaseAction = new LocalizableAction("touppercase", "touppercaseDesc", prov) {

        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            caseActions("toUpperCase");

        }
    };

    /**
     * Action for making lowercased text out of selected text
     */
    private final Action toLowerCaseAction = new LocalizableAction("tolowercase", "tolowercaseDesc", prov) {

        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            caseActions("toLowerCase");
        }
    };


    /**
     * Action for making togglecased text out of selected text
     */
    private final Action invertCaseAction = new LocalizableAction("invertcase", "invertcaseDesc", prov) {

        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            caseActions("invertCase");
        }

    };

    //===================================ASC, DESC, UNIQUE =============================================================
    //======================================SORT ASCENDING==============================================================
    /**
     * Action that sort selected lines in ascending order (if only part of line is selected it counts as the whole line
     * was selected)
     */
    private final Action ascendingAction = new LocalizableAction("ascending", prov) {

        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            sortUnique("ascending");
        }
    };

    //======================================SORT DESCENDING=============================================================
    /**
     * Action that sort selected lines in descending order (if only part of line is selected it counts as the whole line
     * was selected)
     */
    private final Action descendingAction = new LocalizableAction("descending", prov) {

        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            sortUnique("descending");
        }

    };

    //==============================================UNIQUE==============================================================
    /**
     * Action that keeps only unique lines out of the selected ones (if only part of line is selected it counts as
     * the whole line  was selected)
     */
    private final Action uniqueAction = new LocalizableAction("unique", prov) {

        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            sortUnique("unique");
        }
    };

    /**
     * Method that sorts selected text in ascending or descending order or only keeps unique lines
     *
     * @param action Actions can be ascending, descending, unique
     */
    private void sortUnique(String action) {
        Locale xyLocale = new Locale(prov.getLanguage());
        Collator xyCollator = Collator.getInstance(xyLocale);

        TextEditor doc = multipleDocumentModel.getCurrentDocument().getTextComponent().getTextEditor();
        if (!doc.getTextComponent().hasSelectedText())
            return;


        String text = doc.getTextComponent().getTextFromRange(doc.getTextComponent().getSelectionRange());
        String[] linije = text.split("\n");
        doc.getTextComponent().deleteRange(doc.getTextComponent().getSelectionRange());
        String vrati = switch (action) {
            case "ascending" -> Arrays.stream(linije).sorted(xyCollator).collect(Collectors.joining("\n"));
            case "descending" -> Arrays.stream(linije).sorted(xyCollator.reversed()).collect(Collectors.joining("\n"));
            case "unique" -> Arrays.stream(linije).distinct().collect(Collectors.joining("\n"));
            default -> throw new UnsupportedOperationException();
        };
        doc.getTextComponent().insert(vrati);
    }
//======================================================================================================================


    //==========================================STATISTICS==============================================================
    /**
     * Action used for getting statistics
     */
    private final Action statsAction = new LocalizableAction("statistics", prov) {

        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            int length;
            int spaces = 0;
            int numberOfLines = 1;
            MyJTextArea textArea = multipleDocumentModel.getCurrentDocument().getTextComponent();
            String text = textArea.getTextEditor().getTextComponent().getAllText();

            length = text.length();
            char[] textCharArray = text.toCharArray();

            for (char c : textCharArray) {
                if (Character.isWhitespace(c)) {
                    spaces++;
                }
                if (c == '\n') {
                    numberOfLines++;
                }
            }


            JOptionPane.showMessageDialog(
                    JNotepadPP.this,
                    prov.getString("yourdocumenthas") + " " + length + " " + prov.getString("characters") + ",  "
                            + (length - spaces) +
                            " " + prov.getString("nonblankcharactersand") + " " + numberOfLines + " " + prov.getString("lines") + ".",
                    prov.getString("statistics"),
                    JOptionPane.INFORMATION_MESSAGE);

        }
    };


    //===========================================EXIT/ X ===============================================================
    /**
     * Actions used for closing application
     */
    private final Action exitAction = new LocalizableAction("exit", "exitDesc", prov) {

        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            int discard = 0; //0 save //1 don't save //2 discard
            for (int i = 0; i < multipleDocumentModel.getNumberOfDocuments(); i = i + 1) {
                SingleDocumentModel sdm = multipleDocumentModel.getDocument(i);
                tabbedPane.setSelectedIndex(i);
                discard = fileSaveIfModifiedOnClose(sdm);

                if (discard == 2) { //ako je kliknut discard prekini closanje
                    break;
                }
            }

            if (discard != 2) { //ako nije discard onda dispose
                dispose();
                // System.exit(0);
            }
        }
    };

    //===========================================hrAction===============================================================
    /**
     * Action used for setting language of application to Croatian
     */
    private final Action hrAction = new LocalizableAction("hr", "hrDesc", prov) {

        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            LocalizationProvider.getInstance().setLanguage("hr");
            localizationFileChooser();
        }
    };

    //===========================================enAction===============================================================
    /**
     * Action used for setting language of application to English
     */
    private final Action enAction = new LocalizableAction("en", "enDesc", prov) {

        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            LocalizationProvider.getInstance().setLanguage("en");
            localizationFileChooser();
        }
    };

    //===========================================deAction===============================================================
    /**
     * Action used for setting language of application to German
     */
    private final Action deAction = new LocalizableAction("de", "deDesc", prov) {

        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            LocalizationProvider.getInstance().setLanguage("de");
            localizationFileChooser();
        }

    };


    /**
     * Function used for setting FileChooser and other UIManager things to selected language
     */
    private void localizationFileChooser() {
        UIManager.put("FileChooser.saveInLabelText", prov.getString("savein"));
        UIManager.put("FileChooser.acceptAllFileFilterText", prov.getString("alltypes"));
        UIManager.put("FileChooser.lookInLabelText", prov.getString("lookin"));
        UIManager.put("FileChooser.saveButtonText", prov.getString("save"));
        UIManager.put("FileChooser.saveButtonToolTipText", prov.getString("savedesc"));
        UIManager.put("FileChooser.cancelButtonText", prov.getString("cancel"));
        UIManager.put("FileChooser.cancelButtonToolTipText", prov.getString("cancel"));
        UIManager.put("FileChooser.openButtonText", prov.getString("open"));
        UIManager.put("FileChooser.openButtonToolTipText", prov.getString("opendesc"));
        UIManager.put("FileChooser.filesOfTypeLabelText", prov.getString("type"));
        UIManager.put("FileChooser.fileNameLabelText", prov.getString("file"));
        UIManager.put("FileChooser.detailsViewButtonToolTipText", prov.getString("details"));
        UIManager.put("FileChooser.upFolderToolTipText", prov.getString("uponelevel"));
        UIManager.put("FileChooser.upFolderAccessibleName", prov.getString("uponelevel"));
        UIManager.put("FileChooser.fileNameHeaderText", prov.getString("name"));
        UIManager.put("FileChooser.fileSizeHeaderText", prov.getString("size"));
        UIManager.put("FileChooser.fileTypeHeaderText", prov.getString("type"));
        UIManager.put("FileChooser.fileDateHeaderText", prov.getString("date"));
        UIManager.put("FileChooser.fileAttrHeaderText", prov.getString("attributes"));
        UIManager.put("FileChooser.homeFolderToolTipText", prov.getString("home"));
        UIManager.put("FileChooser.newFolderToolTipText", prov.getString("newdirectory"));
    }

    /**
     * Function in which actions are described.  (acceleration key, mnemonic keys...)
     */
    private void createActions() {
        newTabAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control N"));
        newTabAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);

        openDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control O"));
        openDocumentAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);

        saveDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control S"));
        saveDocumentAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);

        saveDocumentAsAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control alt S"));
        saveDocumentAsAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);

        closeAction.putValue(Action.NAME, "Close");
        closeAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control W"));
        closeAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_E);

        selectAllAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control A"));
        selectAllAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_T);

        cutAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_T);
        cutAction.setEnabled(false);

        copyAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
        copyAction.setEnabled(false);
        pasteAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_P);
        pasteAction.setEnabled(false);
        pasteAndRemoveAction.setEnabled(false);

        statsAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control T"));
        statsAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_T);

        hrAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control 0"));
        hrAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);

        enAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control 9"));
        enAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_E);

        deAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control 8"));
        deAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_G);

        toUpperCaseAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control U"));
        toUpperCaseAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_U);

        toLowerCaseAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control L"));
        toLowerCaseAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_L);

        invertCaseAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control F3"));
        invertCaseAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_T);

        ascendingAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control F4"));
        ascendingAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);

        descendingAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control F5"));
        descendingAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_D);

        uniqueAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control F6"));
        uniqueAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_U);
    }

    /**
     * Function used for making menu layouts
     */
    private void createMenus() {
        JMenuBar menuBar = new JMenuBar();

        LJMenu fileMenu = new LJMenu("file", prov);
        menuBar.add(fileMenu);

        fileMenu.add(new JMenuItem(newTabAction));
        fileMenu.add(new JMenuItem(openDocumentAction));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(saveDocumentAction));
        fileMenu.add(new JMenuItem(saveDocumentAsAction));
        fileMenu.add(new JMenuItem(closeAction));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(exitAction));

        JMenu editMenu = new LJMenu("edit", prov);
        menuBar.add(editMenu);
        editMenu.add(new JMenuItem(selectAllAction)); //selectAll
        editMenu.add(new JMenuItem(cutAction)); //cut
        editMenu.add(new JMenuItem(copyAction)); //copy
        editMenu.add(new JMenuItem(pasteAction)); //paste
        editMenu.add(new JMenuItem(pasteAndRemoveAction)); //paste and remove

        //tools
        JMenu toolsMenu = new LJMenu("tools", prov);
        menuBar.add(toolsMenu);

        { //submenu CHANGE CASE
            JMenu changeCaseSubMenu = new LJMenu("changecase", prov);
            toolsMenu.add(changeCaseSubMenu);

            changeCaseSubMenu.add(caretDependingMenuItem(toUpperCaseAction)); //UPPERCASE
            changeCaseSubMenu.add(caretDependingMenuItem(toLowerCaseAction)); //lowercase
            changeCaseSubMenu.add(caretDependingMenuItem(invertCaseAction)); //iNVERTcASE
        }

        { //submenu Sort
            JMenu sortSubMenu = new JMenu("Sort");
            toolsMenu.add(sortSubMenu);

            sortSubMenu.add(caretDependingMenuItem(ascendingAction)); //ascending
            sortSubMenu.add(caretDependingMenuItem(descendingAction)); //descending
        }
        toolsMenu.add(caretDependingMenuItem(uniqueAction)); //unique

        //kraj tools

        JMenu languages = new LJMenu("languages", prov);
        menuBar.add(languages);
        languages.add(hrAction);
        languages.add(enAction);
        languages.add(deAction);

        //=============================================== PLUGINS ======================================================
        JMenu pluginMenu = new JMenu("Plugins");


        String path = "src/main/java/hr/fer/oprpp1/hw08/plugins/hr/fer/oprpp1/hw08/plugins";
        File f = new File(path);
        String[] files = f.list((dir, name) -> name.endsWith(".class"));

        if (files != null) {
            for (String file : files) {
                System.out.println(file);
                if (file.endsWith(".class") && !file.contains("$")) {
                    Plugin p = PluginFactory.newInstance(file.substring(0, file.length() - 6));
                    TextEditor textEditor = multipleDocumentModel.getCurrentDocument().getTextComponent().getTextEditor();

                    Action action = new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            p.execute(textEditor.getTextComponent(),
                                    textEditor.getUndoManager(), textEditor.getClipboard());
                        }
                    };

                    action.putValue(Action.SHORT_DESCRIPTION, p.getDescription());

                    JMenuItem menuItem = new JMenuItem(action);
                    menuItem.setText(p.getName());
                    menuItem.getAccessibleContext().setAccessibleDescription(p.getDescription());

                    pluginMenu.add(menuItem);
                }
            }
        }

        if (pluginMenu.getItemCount() > 0) {  //ako postoji koji plugin, onda pokaži i plugin menu
            menuBar.add(pluginMenu);
        }


        this.setJMenuBar(menuBar);
    }

    /**
     * Used to make JMenuItem which is depanding on the caret
     *
     * @param action action for JMenuItem
     * @return JMenuItem
     */
    private JMenuItem caretDependingMenuItem(Action action) {
        JMenuItem item = new JMenuItem(action);
        item.setEnabled(false);
        disabledEnabled.add(item);
        return item;
    }

    /**
     * Enables/disables all items from the list
     *
     * @param setEnabledTrue enable/disable
     */
    public void changeEnabledState(boolean setEnabledTrue) {
        for (JMenuItem item : disabledEnabled) {
            item.setEnabled(setEnabledTrue);
        }
    }

    /**
     * Creates toolbars
     */
    private void createToolbars() {
        JToolBar toolBar = new JToolBar("Alati");
        toolBar.setFloatable(true);

        //new tab i close
        toolBar.add(new JButton(newTabAction));
        toolBar.add(new JButton(openDocumentAction));
        toolBar.add(new JButton(saveDocumentAction));
        toolBar.add(new JButton(saveDocumentAsAction));

        toolBar.add(new JButton(closeAction));
        toolBar.add(new JButton(exitAction));
        //print
        toolBar.addSeparator();
        toolBar.addSeparator();

        toolBar.add(new JButton(selectAllAction));
        toolBar.add(new JButton(cutAction));
        toolBar.add(new JButton(copyAction));
        toolBar.add(new JButton(pasteAction));

        toolBar.addSeparator();
        toolBar.add(statsAction);

        this.getContentPane().add(toolBar, BorderLayout.PAGE_START);
    }


    /**
     * @param args args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JNotepadPP jpp = new JNotepadPP();
            jpp.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            jpp.setVisible(true);
        });
    }

    /**
     * Used for initializing status bar layout
     */
    private void initializeStatusBar() {
        statusBar.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        statusBar.setLayout(new BorderLayout());
        statusBar.setBackground(Color.lightGray);


        JPanel lineColumnSelected = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lineColumnSelected.setBackground(Color.lightGray);
        lineColumnSelected.add(new LJabel("length", prov)); //length
        lineColumnSelected.add(new JLabel()); //length value
        lineColumnSelected.add(makeLabelForStatusBar("Ln: 1")); // line
        lineColumnSelected.add(makeLabelForStatusBar("Col: 1")); //column
        lineColumnSelected.add(makeLabelForStatusBar("Sel: 0")); //selected
        statusBar.add(lineColumnSelected, BorderLayout.LINE_START);


        //date and time
        JLabel dateTimeLabel = new JLabel();
        String pattern = "yyyy/MM/dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Timer timer = new Timer(1000, a -> dateTimeLabel.setText(simpleDateFormat.format(new Date())));
        timer.start();
        statusBar.add(dateTimeLabel, BorderLayout.LINE_END);
        changeStatusBarValues();

    }

    /**
     * Used for making label for status bar // iskreno ne znam što sam ovdje htio jer ovo sethorizontalTextPosition vjerojatno
     * nema nikakvog efekta
     *
     * @param text text to display on label
     * @return label
     */
    private JLabel makeLabelForStatusBar(String text) {
        JLabel label = new JLabel(text);
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        return label;
    }


    /**
     * Function used for changing status bar values when needed (when caret listener says so) XD
     */
    public void changeStatusBarValues() {
        JPanel nekiPanel = (JPanel) statusBar.getComponent(0);
        LJabel lengthLabel = (LJabel) nekiPanel.getComponent(0);
        JLabel lengthValue = (JLabel) nekiPanel.getComponent(1);
        JLabel lineLable = (JLabel) nekiPanel.getComponent(2);
        JLabel columnLabel = (JLabel) nekiPanel.getComponent(3);
        JLabel selectedLabel = (JLabel) nekiPanel.getComponent(4);
        SingleDocumentModel sdm = multipleDocumentModel.getCurrentDocument();
        Location cursor = sdm.getTextComponent().getTextEditor().getTextComponent().getCursorLocation();


        int selectionRangeLength = 0;
        TextEditor textEditor = sdm.getTextComponent().getTextEditor();

        if (textEditor.getTextComponent().hasSelectedText()) {
            String selectedText = textEditor.getTextComponent().getTextFromRange(textEditor.getTextComponent().getSelectionRange());
            selectionRangeLength = selectedText.length();
        }

        lengthLabel.setText(prov.getString("length"));
        lengthValue.setText(String.valueOf(sdm.getTextComponent().getTextEditor().getTextComponent().getAllText().length()));
        lineLable.setText("Ln: " + cursor.getLineNo());
        columnLabel.setText("Col: " + cursor.getPositionInLine());
        selectedLabel.setText("Sel: " + selectionRangeLength);
    }


    /**
     * Listener method for localization change
     */
    @Override
    public void localizationChanged() {
        changeUnnamedTabsName();
    }

    /**
     * Method that changes tab text values when change happens
     */
    private void changeUnnamedTabsName() {
        if (tabbedPane.getTabCount() == 0)
            return;

        for (int i = 0; i < tabbedPane.getTabCount(); ++i) {
            SingleDocumentModel sdm = multipleDocumentModel.getDocument(i);
            if (sdm.getFilePath() == null) {
                tabbedPane.setTitleAt(i, prov.getString("unnamed"));
            }
        }

        if (multipleDocumentModel.getCurrentDocument().getFilePath() == null) {
            this.setTitle(prov.getString("unnamed"));
        }

    }

}
