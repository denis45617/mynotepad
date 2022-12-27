package hr.fer.oprpp1.hw08.plugins;

import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.Location;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.TextEditorModel;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.clipboard.ClipboardStack;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.undo.UndoManager;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.undo.editActions.EditAction;


public class VelikoSlovo implements Plugin {

    @Override
    public String getName() {
        return "Veliko slovo";
    }

    @Override
    public String getDescription() {
        return "Kapitalizira svaku riječ";
    }

    class VelikoSlovoAction implements EditAction {

        private String textBefore;
        private TextEditorModel textEditorModel;
        private Location cursorLocation;

        public VelikoSlovoAction(String textBefore, TextEditorModel model) {
            this.textBefore = textBefore;
            this.textEditorModel = model;
            this.cursorLocation = new Location(model.getCursorLocation());

        }

        @Override
        public void execute_do() {
            executeVelikoSlovo(textEditorModel);
        }

        @Override
        public void execute_undo() {
            textEditorModel.setText(textBefore);
        }

    }

    @Override
    public void execute(TextEditorModel model, UndoManager undoManager, ClipboardStack clipboardStack) {
        //ovdje nema drugog načina nego spremit cijeli kontekst
        String textBefore = model.getAllText();
        VelikoSlovoAction velikoSlovoAction = new VelikoSlovoAction(textBefore, model);
        undoManager.push(velikoSlovoAction);
        executeVelikoSlovo(model);
        model.setCursorLocation(velikoSlovoAction.cursorLocation);
    }

    private void executeVelikoSlovo(TextEditorModel model) {
        String allText = model.getAllText();

        char[] allTextChars = allText.toCharArray();


        try {
            allTextChars[0] = Character.toUpperCase(allTextChars[0]);

            for (int i = 1; i < allTextChars.length; ++i) {
                if (Character.isWhitespace(allTextChars[i])) {
                    try {
                        allTextChars[i + 1] = Character.toUpperCase(allTextChars[i + 1]);
                    } catch (NullPointerException e) {
                        //do nothing
                    }
                }
            }
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            //do nothing
        }

        model.setText(String.valueOf(allTextChars));
    }

}
