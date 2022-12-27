package hr.fer.oprpp1.hw08.mojaTextAreaComponenta.clipboard;

import hr.fer.oprpp1.custom.collections.ArrayIndexedCollection;
import hr.fer.oprpp1.custom.collections.List;
import hr.fer.oprpp1.custom.collections.ObjectStack;


import java.util.EmptyStackException;


public class ClipboardStack {
    private ObjectStack<String> texts = new ObjectStack<>();
    private List<ClipboardObserver> clipboardListeners = new ArrayIndexedCollection<>();



    public void pushToClipboard(String text) {
        texts.push(text);
        notifyAllClipboardListeners();
    }

    public String getFromClipboardAndRemove() {
        String text;
        try {
            text = texts.pop();
            notifyAllClipboardListeners();
        } catch (EmptyStackException e) {
            return "";
        }
        return text;
    }

    public String getFromClipboardWithoutRemoving() {
        try {
            return texts.peek();
        } catch (EmptyStackException e) {
            return "";
        }
    }

    public void clearClipboard() {
        texts.clear();
        notifyAllClipboardListeners();
    }

    public boolean isEmpty() {
        return texts.isEmpty();
    }

    public void addClipboardListener(ClipboardObserver o) {
        clipboardListeners.add(o);
    }

    public void removeClipboardListener(ClipboardObserver o) {
        clipboardListeners.remove(o);
    }

    private void notifyAllClipboardListeners() {
        for (ClipboardObserver o : clipboardListeners) {
            o.updateClipboard();
        }
    }

}
