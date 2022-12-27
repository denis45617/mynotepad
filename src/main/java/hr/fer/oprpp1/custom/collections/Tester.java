package hr.fer.oprpp1.custom.collections;

/**
 * interface For testing. consist only one method test
 */
public interface Tester<T> {
    /**
     * Method that returns true/false depending on the given parameter
     * @param obj Object to be tested
     * @return true/false, depending on the test
     */
    boolean test(T obj);
}
