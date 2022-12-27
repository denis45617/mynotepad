package hr.fer.oprpp1.hw08.jnotepadpp;

import hr.fer.oprpp1.hw08.mojaTextAreaComponenta.MyJTextArea;
import java.nio.file.Path;

public interface SingleDocumentModel {
    /**
     * Getter for textComponent (MyJTextArea)
     * @return MyJTextAread
     */
    MyJTextArea getTextComponent();

    /**
     * Getter for document path
     * @return path
     */
    Path getFilePath();

    /**
     * Setter for document path
     * @param path path
     */
    void setFilePath(Path path);

    /**
     * Getter for modified flag
     * @return true if file was modified and vice versa
     */
    boolean isModified();

    /**
     * Setter for modified flag
     * @param modified modified status
     */
    void setModified(boolean modified);

    /**
     * Method for adding SingleDocumentListener
     * @param l SingleDocumentListener
     */
    void addSingleDocumentListener(SingleDocumentListener l);

    /**
     * Method for removing one SingleDocumentListener
     * @param l SingleDocumentListener
     */
    void removeSingleDocumentListener(SingleDocumentListener l);
}
