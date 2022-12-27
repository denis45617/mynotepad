package hr.fer.oprpp1.hw08.plugins;

import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.TextEditorModel;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.clipboard.ClipboardStack;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.undo.UndoManager;


public interface Plugin {
    /**
     * Returns plugin name
     *
     * @return name
     */
    String getName();

    /**
     * Returns plugin description
     *
     * @return description
     */
    String getDescription();

    /**
     * Executes plugin task over given TextEditorModel
     *
     * @param model          TextEditorModel
     * @param undoManager    UndoManager
     * @param clipboardStack ClipboardStack
     */
    void execute(TextEditorModel model, UndoManager undoManager, ClipboardStack clipboardStack);
}
