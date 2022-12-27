package hr.fer.oprpp1.hw08.mojaTextAreaComponenta;

import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.clipboard.ClipboardStack;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.cursor.CursorAction;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.cursor.CursorObserver;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.undo.UndoManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;


public class TextEditor extends JComponent implements CursorObserver, KeyListener {
    private FontMetrics fm;
    private int TEXT_HEIGHT;
    private final TextEditorModel textEditorModel;
    private Graphics2D g2d;
    private boolean ctrlIsClicked = false;
    private final ClipboardStack clipboard;
    private final UndoManager undoManager = new UndoManager();
    private boolean cursorVisible = true;
    private Timer timer;

    /**
     * TextEditor constructor
     *
     * @param model TextEditorModel
     */
    public TextEditor(TextEditorModel model) {
        this.textEditorModel = model;
        this.clipboard = new ClipboardStack();
        model.addCursorListener(this);
        model.addSelectionListener(this::repaint);
        this.addKeyListener(this);
        this.setFocusable(true);

        makeCursorBlink();
    }

    /**
     * This function makes cursor blink every 530 milliseconds. If there was no action for 5 seconds in a row cursor stops blinking.
     * Every cursor move restarts cursor blinking.
     */
    private void makeCursorBlink() {
        AtomicLong t0 = new AtomicLong(System.currentTimeMillis());
        timer = new Timer(530, e -> {   //navodno je 530 default na windowsu... bilo bi fora iz sustava isčitati nekako
            if (System.currentTimeMillis() - t0.get() > 5000) {
                timer.stop();
                cursorVisible = true;
                repaint();
            } else {
                cursorVisible = !cursorVisible;
                this.repaint();
            }
        });
        timer.start();

        textEditorModel.addCursorListener((loc, act) -> {
            t0.set(System.currentTimeMillis());
            timer.restart();
            cursorVisible = true;  //inače se odmah prebaci sa true na false i ne vidi se dok se pomiče
        });
    }

    /**
     * ClipboardStack getter
     *
     * @return ClipboardStack
     */
    public ClipboardStack getClipboard() {
        return clipboard;
    }

    /**
     * UndoManager getter
     *
     * @return UndoManager
     */
    public UndoManager getUndoManager() {
        return undoManager;
    }

    /**
     * FontMetrics getter
     *
     * @return FontMetrics
     */
    public FontMetrics getFm() {
        return fm;
    }

    /**
     * TextEditorModel getter
     *
     * @return TextEditorModel
     */
    public TextEditorModel getTextComponent() {
        return textEditorModel;
    }

    public void paintComponent(Graphics g) {
        this.requestFocusInWindow();
        super.paintComponent(g);
        g2d = (Graphics2D) g;
        g2d.setBackground(Color.WHITE);

        Font yFont = new Font("Serif", Font.BOLD, 13);
        g2d.setFont(yFont);
        fm = g2d.getFontMetrics();
        TEXT_HEIGHT = fm.getHeight();

        if (textEditorModel.hasSelectedText())
            drawSelectionBackground();
        drawText();
        if (cursorVisible)
            drawCursor();
    }

    /**
     * Draws background for selected text
     */
    private void drawSelectionBackground() {
        LocationRange selectedText = textEditorModel.getSelectionRange();

        Location start = selectedText.getSelectionRangeStart();
        Location end = selectedText.getSelectionRangeEnd();

        Location smaller = Location.getSmaller(start, end);
        Location bigger = Location.getBigger(start, end);
        int firstLineIndex = smaller.getLineNo();
        int lastLineIndex = bigger.getLineNo();
        int currentLineIndex = firstLineIndex;

        Iterator<String> lines = textEditorModel.linesRange(smaller.getLineNo(), bigger.getLineNo() + 1);


        while (lines.hasNext()) {
            String linija = lines.next();

            int razmakLijevo = 0;
            int width = 0;
            //ako je sve unutar iste linije onda...
            if (firstLineIndex == lastLineIndex) {
                razmakLijevo = fm.stringWidth(linija.substring(0, smaller.getPositionInLine()));
                width = fm.stringWidth(linija.substring(smaller.getPositionInLine(), bigger.getPositionInLine()));
            }

            //inače se selektirani tekst proteže kroz više linija
            else if (currentLineIndex == firstLineIndex) {   //ako je prva linija onda treba crtati od toga do kraja te linije
                razmakLijevo = fm.stringWidth(linija.substring(0, smaller.getPositionInLine()));
                width = fm.stringWidth(linija.substring(smaller.getPositionInLine()));
            } else if (currentLineIndex == lastLineIndex) { //ako je zadnja linija onda treba crtati do te točke
                width = fm.stringWidth(linija.substring(0, bigger.getPositionInLine()));
            } else {  //linija koja nije zadnja niti prva
                width = fm.stringWidth(linija);
            }

            g2d.setColor(new Color(0xA2DADA));
            g2d.fillRect(
                    Constants.PADDING + razmakLijevo,
                    Constants.PADDING_TOP + TEXT_HEIGHT * currentLineIndex - fm.getAscent(),
                    width,
                    fm.getAscent() + fm.getDescent() + fm.getLeading()
            );

            currentLineIndex++;
        }

    }

    /**
     * Draws text itself
     */
    private void drawText() {
        g2d.setColor(Color.BLACK);
        int maxStringLength = 0;
        String stringWithMaxLength = "";
        Iterator<String> iterator = textEditorModel.allLines();
        int currentLineNumber = 0;

        while (iterator.hasNext()) {
            String element = iterator.next();
            if (maxStringLength < element.length()) {
                maxStringLength = element.length();
                stringWithMaxLength = element;
            }
            g2d.drawString(element, Constants.PADDING, currentLineNumber++ * TEXT_HEIGHT + Constants.PADDING_TOP);
        }
        this.setPreferredSize(new Dimension(fm.stringWidth(stringWithMaxLength) + 2 * Constants.PADDING,
                currentLineNumber * TEXT_HEIGHT + 2 * Constants.PADDING_TOP));
        this.revalidate();
    }

    /**
     * Draws cursor
     */
    private void drawCursor() {
        Location cursorLocation = textEditorModel.getCursorLocation();
        Iterator<String> lines = textEditorModel.linesRange(cursorLocation.getLineNo(), cursorLocation.getLineNo() + 1);
        String line = lines.next();
        String subLine = line.substring(0, cursorLocation.getPositionInLine());
        int stringWidth = fm.stringWidth(subLine);

        g2d.drawLine(
                Constants.PADDING + stringWidth,
                Constants.PADDING_TOP + TEXT_HEIGHT * cursorLocation.getLineNo(),
                Constants.PADDING + stringWidth,
                Constants.PADDING_TOP + TEXT_HEIGHT * cursorLocation.getLineNo() - fm.getAscent()
        );
    }

    @Override
    public void updateCursorLocation(Location loc, CursorAction action) {
        if (textEditorModel.isSelectionInProgress()) {
            textEditorModel.getSelectionRange().setSelectionRangeEnd(new Location(loc));
        } else {
            textEditorModel.getSelectionRange().setSelectionRangeStart(null);
            textEditorModel.getSelectionRange().setSelectionRangeEnd(null);
        }

        this.repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //DO NOTHING
    }

    //=========================================OVO TREBA MASNO ULJEPŠATI================================================
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {          //strelica GORE
            textEditorModel.moveCursorUp();
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {  //STRELICA DOLJE
            textEditorModel.moveCursorDown();
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {  //STRELICA LIJEVO
            textEditorModel.moveCursorLeft();
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) { //STRELICA DESNO
            textEditorModel.moveCursorRight();
        } else if (e.getKeyCode() == KeyEvent.VK_DELETE) {  //TIPKA DELETE
            undoManager.push(textEditorModel.deleteAfter());
        } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {  //TIPKA BACKSPACE
            if ((textEditorModel.hasSelectedText())) { //obriši sve selektirano
                undoManager.push(textEditorModel.deleteRange(textEditorModel.getSelectionRange()));
            } else {
                undoManager.push(textEditorModel.deleteBefore()); //obriši jedan znak
            }
        } else if (ctrlIsClicked && textEditorModel.isSelectionInProgress() && (e.getKeyCode() == KeyEvent.VK_V)) { // CTRL+SHIFT+V
            if (!pasteAndRemoveAction.isEnabled())
                return;
            pasteAndRemoveAction.actionPerformed(null);
        } else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {   // SHIFT
            if (textEditorModel.getSelectionRange().getSelectionRangeStart() == null)
                textEditorModel.getSelectionRange().setSelectionRangeStart(new Location(textEditorModel.getCursorLocation()));
            textEditorModel.setSelectionInProgress(true);
        } else if (ctrlIsClicked && (e.getKeyCode() == KeyEvent.VK_C)) {    //CTRL+C
            if (!copyAction.isEnabled())
                return;
            copyAction.actionPerformed(null);
        } else if (ctrlIsClicked && (e.getKeyCode() == KeyEvent.VK_A)) {    //CTRL+A
            this.getTextComponent().selectAll();
        } else if (ctrlIsClicked && (e.getKeyCode() == KeyEvent.VK_V)) {    //CTRL+V
            if (!pasteAction.isEnabled())
                return;
            pasteAction.actionPerformed(null);
        } else if (ctrlIsClicked && (e.getKeyCode() == KeyEvent.VK_X)) {    //CTRL+X
            if (!cutAction.isEnabled()) {
                return;
            }
            cutAction.actionPerformed(null);
        } else if (ctrlIsClicked && (e.getKeyCode() == KeyEvent.VK_Z)) {    //CTRL+Z
            if (!undoAction.isEnabled()) {
                return;
            }
            undoAction.actionPerformed(null);
        } else if (ctrlIsClicked && (e.getKeyCode() == KeyEvent.VK_Y)) {    //CTRL+Y
            if (!redoAction.isEnabled())
                return;
            redoAction.actionPerformed(null);
        } else if (e.getKeyCode() == KeyEvent.VK_CONTROL) { //CTRL
            ctrlIsClicked = true;
        } else {
            if (e.getKeyCode() != KeyEvent.VK_CAPS_LOCK) {
                undoManager.push(textEditorModel.insert(e.getKeyChar()));
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            textEditorModel.setSelectionInProgress(false);
        }
        if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
            this.ctrlIsClicked = false;
        }
    }

    /**
     * UndoAction - cancel previous action
     */
    public final Action undoAction = new AbstractAction("Undo") {
        @Override
        public void actionPerformed(ActionEvent e) {
            undoManager.undo();
        }
    };

    /**
     * Redo action - redoes canceled action
     */
    public final Action redoAction = new AbstractAction("Redo") {
        @Override
        public void actionPerformed(ActionEvent e) {
            undoManager.redo();
        }
    };

    /**
     * CopyAction - pushes selected text to the clipboard stack
     */
    public final Action copyAction = new AbstractAction("Copy") {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedText = textEditorModel.getTextFromRange(textEditorModel.getSelectionRange());
            clipboard.pushToClipboard(selectedText);
        }
    };

    /**
     * Gets text from clipboard stack and inserts it in cursor location
     */
    public final Action pasteAction = new AbstractAction("Paste") {
        @Override
        public void actionPerformed(ActionEvent e) {
            String textFromClipboard = clipboard.getFromClipboardWithoutRemoving();
            undoManager.push(textEditorModel.insert(textFromClipboard));
            textEditorModel.getSelectionRange().setSelectionRangeStart(new Location(textEditorModel.getCursorLocation()));
        }
    };

    /**
     * Gets text from clipboard stack and removes it from the stack. Then if proceeds to insert it in cursor location
     */
    public final Action pasteAndRemoveAction = new AbstractAction("xPaste") {
        @Override
        public void actionPerformed(ActionEvent e) {
            String textFromClipboard = clipboard.getFromClipboardAndRemove();
            undoManager.push(textEditorModel.insert(textFromClipboard));
        }
    };

    /**
     * Cuts selected text and pushes it to clipboardStack
     */
    public final Action cutAction = new AbstractAction("Cut") {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedText = textEditorModel.getTextFromRange(textEditorModel.getSelectionRange());  //dohvati označeni teksts
            clipboard.pushToClipboard(selectedText);                            //stavi u clipboard
            undoManager.push(textEditorModel.deleteRange(textEditorModel.getSelectionRange()));   //obriši označeno tekst
        }
    };

    /**
     * Deletes selected text
     */
    public final Action deleteSelectionAction = new AbstractAction("Delete selected") {
        @Override
        public void actionPerformed(ActionEvent e) {
            undoManager.push(textEditorModel.deleteRange(textEditorModel.getSelectionRange()));   //obriši označeno tekst
        }
    };

    /**
     * Clears whole document
     */
    public final Action clearAction = new AbstractAction("Clear") {
        @Override
        public void actionPerformed(ActionEvent e) {
            textEditorModel.selectAll();
            undoManager.push(textEditorModel.deleteRange(textEditorModel.getSelectionRange()));   //obriši označeno tekst
        }
    };

    /**
     * Moves cursor to the start of the document
     */
    public final Action cursorToDocumentStartAction = new AbstractAction("Move to start") {
        @Override
        public void actionPerformed(ActionEvent e) {
            textEditorModel.setCursorLocation(new Location(0, 0));
        }
    };

    /**
     * Moves cursor to the end of the document
     */
    public final Action cursorToDocumentEndLocation = new AbstractAction("Move to end") {
        @Override
        public void actionPerformed(ActionEvent e) {
            int lastLineIndex = textEditorModel.getNumberOfLines() - 1;
            String lastLineText = textEditorModel.getLine(lastLineIndex);
            int endOfTheTextIndex = lastLineText.length();
            textEditorModel.setCursorLocation(new Location(lastLineIndex, endOfTheTextIndex));
        }
    };
}
