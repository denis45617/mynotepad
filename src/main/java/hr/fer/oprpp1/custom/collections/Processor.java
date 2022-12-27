package hr.fer.oprpp1.custom.collections;

/**
 * interface For processing. consist only one method process
 */
public interface Processor<T> {

    /**
     * Method that processes given object. Returns nothing. Just processes given object
     * @param value Object to be processed
     */
    public void process(T value);
}
