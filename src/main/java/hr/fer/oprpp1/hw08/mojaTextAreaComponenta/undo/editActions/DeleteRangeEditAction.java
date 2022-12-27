package hr.fer.oprpp1.hw08.mojaTextAreaComponenta.undo.editActions;

import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.Location;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.LocationRange;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.TextEditorModel;

public record DeleteRangeEditAction(Location cursorLocationBefore, Location cursorLocationAfter,
                                    String removedText,
                                    TextEditorModel textEditorModel) implements EditAction {


    @Override
    public void execute_do() {
        textEditorModel.deleteRange(new LocationRange(new Location(cursorLocationBefore), new Location(cursorLocationAfter)));
    }

    @Override
    public void execute_undo() {
        Location smaller = Location.getSmaller(new Location(cursorLocationBefore), new Location(cursorLocationAfter));
        textEditorModel.setCursorLocation(new Location(smaller));
        textEditorModel.insert(removedText);
        textEditorModel.setCursorLocation(new Location(Location.getBigger(cursorLocationBefore, cursorLocationAfter)));
        textEditorModel.getSelectionRange().setSelectionRangeStart(new Location(Location.getSmaller(cursorLocationBefore, cursorLocationAfter)));
        textEditorModel.getSelectionRange().setSelectionRangeEnd(new Location(Location.getBigger(cursorLocationBefore, cursorLocationAfter)));
    }
}
