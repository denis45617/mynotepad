package hr.fer.oprpp1.hw08.mojaTextAreaComponenta.undo.editActions;


import hr.fer.oprpp1.custom.collections.List;

public record EditActionComposite(List<EditAction> editActionList) implements EditAction {

    @Override
    public void execute_do() {
        for (EditAction action : editActionList) {
            action.execute_do();
        }

    }

    @Override
    public void execute_undo() {
        for (int i = editActionList.size() - 1; i >= 0; --i) {
            editActionList.get(i).execute_undo();
        }

    }
}
