package hr.fer.oprpp1.hw08.jnotepadpp;

/**
 * SingleDocumentListener
 */
public interface SingleDocumentListener {

    /**
     * Listener for document modified status
     * @param model  document
     */
    void documentModifyStatusUpdated(SingleDocumentModel model);

    /**
     * Listeners for document path updated
     * @param model document
     */
    void documentFilePathUpdated(SingleDocumentModel model);
}
