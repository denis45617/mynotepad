package hr.fer.oprpp1.hw08.jnotepadpp;

/**
 * MultipleDocumentListener contains 3 methods
 */
public interface MultipleDocumentListener {
    /**
     * Listener for current document changed
     * @param previousModel previous document
     * @param currentModel new document
     */
    public void currentDocumentChanged(SingleDocumentModel previousModel,
                                SingleDocumentModel currentModel);

    /**
     * Listener for document added
     * @param model SingleDocumentModel
     */
    public void documentAdded(SingleDocumentModel model);

    /**
     * Listener for document removed
     * @param model SingleDocumentModel
     */
    public void documentRemoved(SingleDocumentModel model);
}
