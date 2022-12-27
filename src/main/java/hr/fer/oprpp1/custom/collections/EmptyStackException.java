package hr.fer.oprpp1.custom.collections;

/**
 * Exception used along ObjectStack. Has two variants of constructor, one takes message and one does not take anything
 */
public class EmptyStackException extends RuntimeException{
    /***
     * Public constructor for EmptyStackException
     */
    public EmptyStackException() {
    }

    /**
     * Public constructor for EmptyStackException. Takes message
     * @param message  Exception message
     */
    public EmptyStackException(String message) {
        super(message);
    }


}
