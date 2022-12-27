package hr.fer.oprpp1.custom.collections;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Custom collection based on array
 */
public class ArrayIndexedCollection<T> implements List<T> {
    private int size;
    private T[] elements;
    private long modificationCount = 0;



    /**
     * Class that gives getter ability. Consists of constructor and implements methods hasNextElement and getNextElement
     */
    public static class MyElementsGetter<T> implements ElementsGetter<T> {
        private ArrayIndexedCollection<T> collection;
        private int counter = 0;
        private final long savedModificationCountGetter;

        /**
         * Constructor for MyElementsGetter. Gets 2 parameters
         *
         * @param arrayIndexedCollection Collection we are making getter for
         */
        public MyElementsGetter(ArrayIndexedCollection<T> arrayIndexedCollection) {
            collection = arrayIndexedCollection;
            savedModificationCountGetter = arrayIndexedCollection.modificationCount;
        }

        @Override
        public boolean hasNextElement() {
            if (savedModificationCountGetter != collection.modificationCount) {
                throw new ConcurrentModificationException();
            }
            return counter < collection.size();
        }

        @Override
        public T getNextElement() {
            if (!hasNextElement()) {
                throw new NoSuchElementException();
            }
            return collection.get(counter++);
        }
    }


    /**
     * Constructor for that will take this object method was called on and make a getter for it
     *
     * @return ElementsGetter
     */
    @Override
    public ElementsGetter<T> createElementsGetter() {
        return new MyElementsGetter<>(this);
    }


    /**
     * Constructor used to make array make to the default 16
     */
    public ArrayIndexedCollection() {
        this(16);
    }

    /**
     * Constructor that allows you to set initial capacity
     *
     * @param initialCapacity - initial capacity of the array used in the list
     * @throws IllegalArgumentException when <b>initialCapacity</b> parameter is lower than 1
     */
    @SuppressWarnings("unchecked")
    public ArrayIndexedCollection(int initialCapacity) {
        if (initialCapacity < 1)
            throw new IllegalArgumentException();
        else {
            this.elements = (T[]) new Object[initialCapacity];
            this.size = 0;
        }

    }

    /**
     * Constructor that allows you to make new collection and will copy all elements from the other collection in it
     *
     * @param someOtherCollection - collection to be copied into the new one
     */
    public ArrayIndexedCollection(Collection<? extends T> someOtherCollection) {
        this(someOtherCollection, 0);
    }


    /**
     * Constructor
     * if the initialCapacity is smaller than the size of the given collection, the size of
     * the given collection should be used for elements array preallocation. If the given collection is null, a
     * NullPointerException should be thrown
     *
     * @param someOtherCollection - collection to be copied into the new one
     * @param initialCapacity     -   initial capacity of the array used in the list
     * @throws NullPointerException when null pointer is sent as <b>someOtherCollection</b>
     */

    @SuppressWarnings("unchecked")
    public ArrayIndexedCollection(Collection<? extends T> someOtherCollection, int initialCapacity) {
        if (someOtherCollection == null) {
            throw new NullPointerException();
        }

        if (someOtherCollection.size() > initialCapacity) {
            this.elements = (T[]) new Object[someOtherCollection.size()];
        } else {
            this.elements = (T[]) new Object[initialCapacity];
        }

        this.size = 0; //just making sure :)
        this.addAll(someOtherCollection);
    }


    @Override
    public int size() {
        return size;
    } //more or less getter method


    /**
     * Adds the given object into this collection (reference is added into first empty place in the elements array;
     *
     * @param value Object to be added into collection
     * @throws NullPointerException for null pointer
     */

    @Override
    public void add(T value) {
        if (value == null) {
            throw new NullPointerException();
        }
        if (this.size < this.elements.length) {             //if there is a free space
            this.elements[size] = value;                    //just add a new object to it
            ++(this.size);                                  //and increase size
            ++modificationCount;
            return;
        }

        // failed to find empty space.
        makeElementsArrayDoubleTheSize();
        this.elements[size] = value;
        ++(this.size);                                                                  //and increase the size value
        ++modificationCount;
    }

    @Override
    public boolean contains(Object value) {
        if (value == null) {
            return this.size < this.elements.length;
        } else {
            for (int i = 0; i < this.size; ++i) {
                if (this.elements[i].equals(value))
                    return true;
            }


            return false;
        }
    }

    @Override
    public boolean remove(Object value) {
        if (this.contains(value)) {
            for (int i = 0; i < this.size; ++i) {
                if (this.elements[i].equals(value)) {
                    for (; i < this.elements.length - 1; ++i) {
                        this.elements[i] = this.elements[i + 1];
                    }
                    this.size--;
                    ++modificationCount;
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public Object[] toArray() {
        if (!(size > 0))
            throw new UnsupportedOperationException("No elements in array");

        Object[] arrayToBeReturned = new Object[this.size];
        System.arraycopy(this.elements, 0, arrayToBeReturned, 0, this.size);
        return arrayToBeReturned;

    }


    @Override
    public void clear() {
        for (int i = 0; i < this.size; ++i) {
            this.elements[i] = null;
        }
        this.size = 0;
        ++modificationCount;
    }


    /**
     * Returns the object that is stored in backing array at position index.
     *
     * @param index - index in the list from which you want to get an element
     * @return element of the list from the given index
     * @throws IndexOutOfBoundsException when given index is lower than 0 or greater than size-1
     */
    public T get(int index) {
        if (index < 0 || index > (this.size - 1)) {
            throw new IndexOutOfBoundsException();
        }
        return this.elements[index];

    }

    /**
     * <b>Inserts</b> <i>(does not overwrite)</i> the given value at the given <b>position</b> in array.
     *
     * @param value    Object to be inserted
     * @param position Position on which object will be inserted on
     * @throws IndexOutOfBoundsException when index (position) is lower than 0 or greater than size of the collection
     */
    public void insert(T value, int position) {
        if (position < 0 || position > this.size) {
            throw new IndexOutOfBoundsException();
        }

        if (this.elements.length == this.size) {
            makeElementsArrayDoubleTheSize();
            this.size++;
            ++modificationCount;
            for (int i = size - 1; i > position; --i) {
                this.elements[i] = this.elements[i - 1];
            }
        } else {
            this.size++;
            ++modificationCount;
            for (int i = size - 1; i > position; --i) {
                this.elements[i] = this.elements[i - 1];
            }
        }
        this.elements[position] = value;
    }


    /**
     * Finds index of the asked parameter (only one)
     *
     * @param value paramter you want to find index of
     * @return index of the given paramter if it is contained, or -1 if given value is not in the collection
     */

    public int indexOf(Object value) {
        for (int i = 0; i < this.size; ++i) {
            if (this.elements[i].equals(value))
                return i;
        }
        return -1;
    }

    /**
     * Removed element in the collection whoes index was given as a parameter
     *
     * @param index index fo paramter to be removed
     * @throws IndexOutOfBoundsException when given index is lower than 0 or greater/equal to the size of collection
     */
    public void remove(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException();
        } else {
            for (int i = index; i < this.size; ++i) {
                this.elements[i] = this.elements[i + 1];
            }
            this.size--;
            ++modificationCount;
        }
    }

    /**
     * helper method that doubles the size of the collection
     */
    @SuppressWarnings("unchecked")
    private void makeElementsArrayDoubleTheSize() {
        T[] newArray = (T[]) new Object[2 * this.elements.length];                      //create the new array  that has double the size
        System.arraycopy(this.elements, 0, newArray, 0, this.elements.length);  //copy all the objects from the old one to it
        this.elements = newArray;                                                     //make a new array the "main" one
        ++modificationCount;
    }

    @Override
    public Iterator<T> iterator() {
        ElementsGetter<T> elementsGetter = createElementsGetter();
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return elementsGetter.hasNextElement();
            }

            @Override
            public T next() {
                return elementsGetter.getNextElement();
            }
        };
    }
}






