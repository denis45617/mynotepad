package hr.fer.oprpp1.hw08.mojaTextAreaComponenta.undo;

import hr.fer.oprpp1.custom.collections.ArrayIndexedCollection;
import hr.fer.oprpp1.custom.collections.List;
import hr.fer.oprpp1.custom.collections.ObjectStack;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.undo.editActions.EditAction;


import java.util.EmptyStackException;


public class UndoManager {

    private final ObjectStack<EditAction> undoStack = new ObjectStack<>();
    private final ObjectStack<EditAction> redoStack = new ObjectStack<>();

    private final List<StackListener> undoStackListeners = new ArrayIndexedCollection<>();
    private final List<StackListener> redoStackListeners = new ArrayIndexedCollection<>();


    public void addUndoStackListener(StackListener listener) {
        undoStackListeners.add(listener);
    }

    public void removeUndoStackListener(StackListener listener) {
        undoStackListeners.remove(listener);
    }

    public void notifyAllUndoStackListeners() {
        StackState stackState;
        if (undoStack.isEmpty()) {
            stackState = StackState.EMPTY;
        } else {
            stackState = StackState.NOT_EMPTY;
        }
        for (StackListener listener : undoStackListeners) {
            listener.actionPerformed(stackState);
        }
    }

    public void notifyAllRedoStackListeners() {
        StackState stackState;
        if (redoStack.isEmpty()) {
            stackState = StackState.EMPTY;
        } else {
            stackState = StackState.NOT_EMPTY;
        }

        for (StackListener listener : redoStackListeners) {
            listener.actionPerformed(stackState);
        }
    }


    public void addRedoStackListener(StackListener listener) {
        redoStackListeners.add(listener);
    }


    public void removeRedoStackListener(StackListener listener) {
        redoStackListeners.remove(listener);
    }

    public void undo() {
        try {
            EditAction editAction = undoStack.pop();
            redoStack.push(editAction);
            editAction.execute_undo();

            notifyAllUndoStackListeners();
            notifyAllRedoStackListeners();

        } catch (EmptyStackException e) {
            //DO NOTHING
        }
    }

    public void redo() {
        try {
            EditAction editAction = redoStack.pop();
            editAction.execute_do();
            undoStack.push(editAction);


            notifyAllRedoStackListeners();
            notifyAllUndoStackListeners();

        } catch (EmptyStackException e) {
            //DO NOTHING
        }
    }

    public void push(EditAction action) {
        if (action == null)
            return;
        redoStack.clear();
        undoStack.push(action);
        notifyAllUndoStackListeners();
    }

}
