package hr.fer.oprpp1.hw08.mojaTextAreaComponenta;

import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.cursor.CursorAction;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.cursor.CursorObserver;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;

public class MyJTextArea extends JPanel {
    private final TextEditorModel textEditorModel;
    private final TextEditor textEditor;

    /**
     * MyJTextArea constructor
     */
    public MyJTextArea() {
        this.setLayout(new BorderLayout());

        textEditorModel = new TextEditorModel("");

        textEditor = new TextEditor(textEditorModel);
        JScrollPane jScrollPane = new JScrollPane(textEditor);

        textEditorModel.addCursorListener(new CursorObserver() {
            @Override
            public void updateCursorLocation(Location loc, CursorAction move_action) {
                if (move_action == CursorAction.MOVE_HORIZONTAL_AND_VERTICAL || move_action == CursorAction.MOVE_HORIZONTAL) {
                    JScrollBar horizontalScrollBar = jScrollPane.getHorizontalScrollBar();
                    if (horizontalScrollBar != null) {
                        int horizontalScrollBarMinimumValue = horizontalScrollBar.getMinimum();

                        Location cursorLocation = textEditorModel.getCursorLocation();
                        Iterator<String> iterator = textEditorModel.linesRange(cursorLocation.getLineNo(), cursorLocation.getLineNo() + 1);
                        String linija = "";
                        if (iterator.hasNext())
                            linija = iterator.next();

                        FontMetrics fm = textEditor.getFm();
                        if (fm == null) return;
                        int textWidth = fm.stringWidth(linija.substring(0, cursorLocation.getPositionInLine()));
                        if (textWidth <= jScrollPane.getWidth() - 100) {
                            horizontalScrollBar.setValue(0);
                        } else {
                            horizontalScrollBar.setValue(horizontalScrollBarMinimumValue + textWidth - 100);
                        }
                    }
                }

                if (move_action == CursorAction.MOVE_HORIZONTAL_AND_VERTICAL || move_action == CursorAction.MOVE_VERTICAL) {
                    JScrollBar verticalScrollBar = jScrollPane.getVerticalScrollBar();
                    if (verticalScrollBar != null) {
                        int verticalScrollBarMinimumValue = verticalScrollBar.getMinimum();

                        Location cursorLocation = textEditorModel.getCursorLocation();
                        FontMetrics fm = textEditor.getFm();

                        int textHeight = cursorLocation.getLineNo() * fm.getHeight();
                        if (textHeight <= jScrollPane.getHeight() - 100) {
                            verticalScrollBar.setValue(0);
                        } else {
                            verticalScrollBar.setValue(verticalScrollBarMinimumValue + textHeight);
                        }
                    }
                }
            }
        });

        this.add(jScrollPane);
    }

    /**
     * TextEditor getter
     *
     * @return TextEditor
     */
    public TextEditor getTextEditor() {
        return textEditor;
    }

}
