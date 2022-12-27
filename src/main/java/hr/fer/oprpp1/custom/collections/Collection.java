package hr.fer.oprpp1.custom.collections;

/**
 * public interface for implementing custom collections. Contains methods add, remove, insert isEmpty() etc
 */
public interface Collection<T> {

    /**
     * Returns true if collection contains no objects and false otherwise.
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns the number of currently stored objects in this collections.
     */
    int size();

    /**
     * Adds the given object into this collection.
     *
     * @param value to be added
     */
    void add(T value);

    /**
     * Returns true only if the collection contains given value, as determined by equals method.
     * It is OK to ask if collection contains null.
     *
     * @param value to be checked if it is contained or not
     * @return true if contained in collection, false if not contained
     */
    boolean contains(Object value);


    /**
     * Returns true only if the collection contains given value as determined by equals method and removes
     * *one occurrence of it (in this class it is not specified which one).
     *
     * @param value - value to be removed (only one object ad the time)
     * @return true if value was found and remove from collection, false otherwise
     */
    boolean remove(Object value);


    /**
     * Allocates new array which size equals to the size of this collections, fills it with collection content and
     * returns the array. This method never returns null.
     * Implement it here to throw UnsupportedOperationException.
     *
     * @return array that contains all elements of the list
     */
    Object[] toArray();

    /**
     * Method calls processor.process(.) for each element of this collection. The order in which elements
     * will be sent is undefined in this class.
     */
    default void forEach(Processor<T> processor){   // ne valja parametrizacija
        ElementsGetter<? extends T> getter = this.createElementsGetter();
        while(getter.hasNextElement()){
            processor.process(getter.getNextElement());
        }
    }


    /**
     * Method adds into the current collection all elements from the given collection. This other collection remains unchanged.
     * <p>
     * Implement it here to define a local processor class
     * whose method process will add each item into the current collection by calling method add, and then call
     * forEach on the other collection with this processor as argument.
     *
     * @param other - other collection
     */


    default void addAll(Collection<? extends T> other) {
        class Processor2<K extends T> implements Processor<K> {
            public void  process(K value) {
                add(value);
            }
        }
        other.forEach(new Processor2<>());
    }





    /**
     * Removes all elements from this collection.
     */
    void clear();


    /**
     * Creates elements getter
     * @return ElementsGetter
     */
    ElementsGetter<T> createElementsGetter();

    /**
     * Adds all satisfying (for the tester) in given collection
     * @param col given collection
     * @param tester  tester
     */
    default void addAllSatisfying(Collection<? extends  T> col, Tester<? super T> tester) {
        ElementsGetter<? extends T> getter = col.createElementsGetter();
        while (getter.hasNextElement()) {
            T element = getter.getNextElement();
            if (tester.test(element)) {
                this.add(element);
            }
        }

    }


}
