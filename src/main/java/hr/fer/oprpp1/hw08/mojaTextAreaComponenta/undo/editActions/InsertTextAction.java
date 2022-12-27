package hr.fer.oprpp1.hw08.mojaTextAreaComponenta.undo.editActions;

import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.Location;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.LocationRange;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.TextEditorModel;

public record InsertTextAction(Location cursorLocationBefore, Location cursorLocationAfter,
                               String insertedText,
                               TextEditorModel textEditorModel) implements EditAction {


    @Override
    public void execute_do() {
        textEditorModel.setCursorLocation(new Location(cursorLocationBefore));
        textEditorModel.insert(insertedText);
    }

    @Override
    public void execute_undo() {
        textEditorModel.deleteRange(new LocationRange(new Location(cursorLocationBefore), new Location(cursorLocationAfter)));
        textEditorModel.setCursorLocation(new Location(cursorLocationBefore));
    }
}
