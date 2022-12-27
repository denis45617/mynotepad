package hr.fer.oprpp1.hw08.plugins;

import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.TextEditorModel;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.clipboard.ClipboardStack;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.undo.UndoManager;


import javax.swing.*;

public class Statistika implements Plugin {

    @Override
    public String getName() {
        return "Statistics";
    }

    @Override
    public String getDescription() {
        return "Shows statistics in new window";
    }

    @Override
    public void execute(TextEditorModel model, UndoManager undoManager, ClipboardStack clipboardStack) {
        int length;
        int spaces = 0;
        int numberOfLines = 1;

        String text = model.getAllText();

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
                null,
                "Your document has  " + length + " characters " + ",  "
                        + (length - spaces) +
                        " non-blank characters and " + numberOfLines + " lines.",
                "Statistics",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}




