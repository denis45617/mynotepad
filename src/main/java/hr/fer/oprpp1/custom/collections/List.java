package hr.fer.oprpp1.custom.collections;

import java.util.Iterator;

/**
 * Interface that allows implementing List, has typical list methods  get, insert, indexOf, remove
 */
public interface List<T> extends Collection<T>, Iterable<T> {
    /**
     * Gets object from the given index in the collection
     *
     * @param index index from which you want to get Object
     * @return Object that is stored in collection on the given index
     */
    T get(int index);

    /**
     * Inserts (does not overwrite) object to the given position in the collection
     *
     * @param value    Object to be stored on the given position
     * @param position position in the collection
     */
    void insert(T value, int position);

    /**
     * Returns index of first appearance of the given object in collection
     *
     * @param value index if found, -1 otherwise
     * @return int index
     */
    int indexOf(Object value);

    /**
     * Removes element from the given index, all elements that were right of the element on the given object m
     * ove one spot to the left
     *
     * @param index index of the element that you want to be removed
     */
    void remove(int index);
}
