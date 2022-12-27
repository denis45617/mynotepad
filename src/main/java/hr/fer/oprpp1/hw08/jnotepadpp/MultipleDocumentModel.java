package hr.fer.oprpp1.hw08.jnotepadpp;

import javax.swing.*;
import java.nio.file.Path;

/**
 * MultipleDocumentModel interface
 */
public interface MultipleDocumentModel extends Iterable<SingleDocumentModel> {
    JComponent getVisualComponent();


    /**
     * Method used for creating new document
     * @return SingleDocumentModel new document
     */
    SingleDocumentModel createNewDocument();


    /**
     * Method used for getting current document
     * @return current document (opened document)
     */
    SingleDocumentModel getCurrentDocument();

    /**
     * Method used for loading existing document from disk
     * @param path path
     * @return SingleDocumentModel
     */
    SingleDocumentModel loadDocument(Path path);

    /**
     * Method used for saving document to disk. (To save as a new file set newPath to null)
     * @param model  document
     * @param newPath path to save on. null = save as new file
     */
    void saveDocument(SingleDocumentModel model, Path newPath);

    /**
     * Method used for closing document
     * @param model SingleDocumentModel document
     */
    void closeDocument(SingleDocumentModel model);

    /**
     * Method used for adding new MultipleDocumentListener
     * @param l MultipleDocumentListener
     */
    void addMultipleDocumentListener(MultipleDocumentListener l);

    /**
     * Method used for removing MultipleDocumentListener
     * @param l MultipleDocumentListener
     */

    void removeMultipleDocumentListener(MultipleDocumentListener l);

    /**
     * Method used for getting number of opened documents in tabbed pane
     * @return number of opened documents
     */

    int getNumberOfDocuments();

    /**
     * Method used for getting document on given index
     * @param index index
     * @return SingleDocumentModel document on given index
     */
    SingleDocumentModel getDocument(int index);

    /**
     * Method that finds SingleDocumentModel for given path
     * @param path path
     * @return  SingleDocumentModel document, null, if no such model exists
     */
    SingleDocumentModel findForPath(Path path);

    /**
     * Finds index of document
     * @param doc SingleDocumentModel document
     * @return index of document, -1 if not present
     */
    int getIndexOfDocument(SingleDocumentModel doc);
}
