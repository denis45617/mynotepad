package hr.fer.oprpp1.hw08.jnotepadpp;


import hr.fer.oprpp1.custom.collections.LinkedListIndexedCollection;
import hr.fer.oprpp1.custom.collections.List;
import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.MyJTextArea;

import java.nio.file.Path;




public class DefaultSingleDocumentModel implements SingleDocumentModel {
    private Path filePath;
    private final MyJTextArea textArea;
    private boolean isModified = false;
    private final List<SingleDocumentListener> singleDocumentListeners = new LinkedListIndexedCollection<>();

    public DefaultSingleDocumentModel(Path p, String content) {
        this.filePath = p;
        this.textArea = new MyJTextArea();
        this.textArea.getTextEditor().getTextComponent().setText(content);


        //tu treba pretplatiti i na listenere
        this.textArea.getTextEditor().getTextComponent().addTextListener(() -> {
            isModified = true;
            notifyAllListenersUpdate();
        });
    }


    private void notifyAllListenersUpdate() {
        for (SingleDocumentListener l : singleDocumentListeners) {
            l.documentModifyStatusUpdated(this);
        }
    }

    @Override
    public MyJTextArea getTextComponent() {
        return textArea;
    }

    @Override
    public Path getFilePath() {
        return filePath;
    }

    @Override
    public void setFilePath(Path path) {
        this.filePath = path;
        notifyAllListenersPath();
    }

    private void notifyAllListenersPath() {
        for (SingleDocumentListener l : singleDocumentListeners) {
            l.documentFilePathUpdated(this);
        }
    }

    @Override
    public boolean isModified() {
        return isModified;
    }

    @Override
    public void setModified(boolean modified) {
        this.isModified = modified;
        notifyAllListenersUpdate();
    }

    @Override
    public void addSingleDocumentListener(SingleDocumentListener l) {
        singleDocumentListeners.add(l);
    }

    @Override
    public void removeSingleDocumentListener(SingleDocumentListener l) {
        singleDocumentListeners.remove(l);
    }
}
