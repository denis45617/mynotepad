package hr.fer.oprpp1.hw08.mojaTextAreaComponenta.undo.editActions;

import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.Location;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.TextEditorModel;

public record DeleteBeforeAction(Location cursorLocationBefore, Location cursorLocationAfter,
                                 char removedChar, TextEditorModel textEditorModel) implements EditAction {


    @Override
    public void execute_do() {
        textEditorModel.setCursorLocation(new Location(cursorLocationBefore));
        textEditorModel.deleteBefore();
    }

    @Override
    public void execute_undo() {
        textEditorModel.setCursorLocation(new Location(cursorLocationAfter));
        textEditorModel.insert(removedChar);
    }
}
