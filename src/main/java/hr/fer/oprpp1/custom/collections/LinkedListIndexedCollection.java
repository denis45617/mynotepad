package hr.fer.oprpp1.custom.collections;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Custom collection based on linklist (connected ListNodes)
 */
public class LinkedListIndexedCollection<T> implements List<T> {
    private int size;
    private ListNode<T> first;
    private ListNode<T> last;
    private long modificationCount = 0;

    /**
     * One element of the list. Contains value and pointers to previous and next element
     */
    private static class ListNode<T> {
        T value;
        ListNode<T> previous;
        ListNode<T> next;

        /**
         * Construcot for ListNode
         *
         * @param value    value to be stored
         * @param previous pointer to previous element
         * @param next     pointer to next element
         */
        private ListNode(T value, ListNode<T> previous, ListNode<T> next) {
            this.value = value;
            this.previous = previous;
            this.next = next;
        }
    }

    /**
     * Getter for list elements
     */
    private static class MyElementsGetter<T> implements ElementsGetter<T> {
        private final LinkedListIndexedCollection<T> collection;
        private ListNode<T> currentNode;
        private final long savedModificationCountGetter;


        /**
         * Constructor for getter
         *
         * @param linkedListIndexedCollection collection
         */
        private MyElementsGetter(LinkedListIndexedCollection<T> linkedListIndexedCollection) {
            collection = linkedListIndexedCollection;
            currentNode = collection.first;
            this.savedModificationCountGetter = linkedListIndexedCollection.modificationCount;

        }

        @Override
        public boolean hasNextElement() {
            if (savedModificationCountGetter != collection.modificationCount) {
                throw new ConcurrentModificationException();
            }
            return currentNode != null;
        }

        @Override
        public T getNextElement() {
            if (!hasNextElement()) {
                throw new NoSuchElementException();
            }

            //big brain time - pomaknem se na sljedeci pa citam s proslog
            currentNode = currentNode.next;
            return (currentNode == null) ? collection.last.value : currentNode.previous.value;

        }
    }

    @Override
    public ElementsGetter<T> createElementsGetter() {
        return new MyElementsGetter<>(this);
    }


    /**
     * Constructor used to initialize collection
     */
    public LinkedListIndexedCollection() {
        this.size = 0;
        this.first = null;
        this.last = null;
    }


    /**
     * Constructor used to make new collection and that will copy all the elements from the collection that is given as and parameter
     *
     * @param someOtherCollection - collection whose elements will be copied into the newly created collection
     * @throws NullPointerException when given parameter is null
     */
    public LinkedListIndexedCollection(Collection<? extends T> someOtherCollection) {
        this();
        if (someOtherCollection == null) {
            throw new NullPointerException();
        }

        this.addAll(someOtherCollection);
        ++modificationCount;
    }


    @Override
    public int size() {
        return size;
    }


    @Override
    public void add(T value) {
        if (value == null) {
            throw new NullPointerException();
        }

        if (this.size == 0) {       //if there was nothing in collection, new object is both first and last
            this.first = this.last = new ListNode<>(value, null, null);
        } else {
            this.last.next = new ListNode<>(value, this.last, null);
            this.last = this.last.next;
        }
        ++size;
        ++modificationCount;
    }


    @Override
    public boolean contains(Object value) {
        if (first == null)
            return false;

        ListNode<T> current = first;
        do {
            if (current.value.equals(value))
                return true;
            current = current.next;
        } while (current != null);
        return false;
    }


    @Override
    public boolean remove(Object value) {

        //if it is first
        if (first.value.equals(value)) {
            this.first = this.first.next;       //let 2nd object to be 1st
            this.first.previous = null;         //now to the first object (that was previously 2nd) set last to be null
            --size;
            ++modificationCount;
            return true;
        }

        //if it is last
        else if (last.value.equals(value)) {
            this.last = this.last.previous;       //let 2nd to last object to be last
            this.last.next = null;                //now to the last object (that was previously 2nd to last) set next to be null
            --size;
            ++modificationCount;
            return true;
        }
        //if it is not either first or last
        else {
            ListNode<T> current = first.next;
            do {
                if (current.value.equals(value)) {
                    current.previous.next = current.next;
                    current.next.previous = current.previous;
                    --size;
                    ++modificationCount;
                    return true;
                }
                current = current.next;
            } while (current != null);
        }

        return false;
    }


    @Override
    public Object[] toArray() {
        if (!(size > 0))
            throw new UnsupportedOperationException();

        Object[] arrayToBeReturned = new Object[this.size];
        ListNode<T> current = this.first;
        int index = 0;
        do {
            arrayToBeReturned[index] = current.value;
            current = current.next;
            ++index;
        } while (current != null);

        return arrayToBeReturned;
    }


    @Override
    public void clear() {
        this.size = 0;
        this.first = this.last = null;
        ++modificationCount;
    }


    /**
     * Returns the object that is stored in the collection at the given index
     *
     * @param index - index in the list from which you want to get an element
     * @return element of the list from the given index
     * @throws IndexOutOfBoundsException when given index is lower than 0 or greater than size-1
     */

    public T get(int index) {
        if (index < 0 || index > size - 1) {
            throw new IndexOutOfBoundsException();
        }

        ListNode<T> current;
        int counter;

        if (index <= size / 2) {
            current = this.first;
            counter = 0;
            while (counter < index) {
                current = current.next;
                counter++;
            }
        } else {
            current = this.last;
            counter = size - 1;
            while (counter > index) {
                current = current.previous;
                counter--;
            }
        }
        return current.value;
    }


    /**
     * <b>Inserts</b> <i>(does not overwrite)</i> the given value at the given <b>position</b> in linked-list.
     *
     * @param value    Object to be inserted
     * @param position Position on which object will be inserted on
     * @throws IndexOutOfBoundsException when index (position) is lower than 0 or greater than size of the collection
     */
    public void insert(T value, int position) {
        if (position < 0 || position > size) {
            throw new IndexOutOfBoundsException();
        }
        if (value == null) throw new NullPointerException("Value ne smije biti null");

        if (this.size == 0) {       //if there was nothing in collection, new object is both first and last
            this.first = this.last = new ListNode<>(value, null, null);
            size++;
            ++modificationCount;
            return;
        }

        //when first
        if (position == 0) {
            this.first = new ListNode<>(value, null, this.first);
            this.first.next.previous = this.first;
            size++;
            ++modificationCount;
            return;
        }

        //when last
        if (position == size) {
            add(value);
            ++modificationCount;
            return;
        }

        ListNode<T> current;
        int counter;

        if (position <= size / 2) {  //ako je u prvoj polovici
            current = this.first;
            counter = 0;
            while (counter < position - 1) {
                current = current.next;
                counter++;
            }
            current.next = new ListNode<>(value, current, current.next);
        } else {                    //ako je u drugoj polovici
            current = this.last;
            counter = size - 1;
            while (counter > position) {
                current = current.previous;
                counter--;
            }
            current.previous.next = new ListNode<>(value, current.previous, current);
        }
        size++;
        ++modificationCount;

    }


    /**
     * Finds index of the asked parameter (only one)
     *
     * @param value parameter you want to find index of
     * @return index of the given parameter if it is contained, or -1 if given value is not in the collection
     */
    public int indexOf(Object value) {
        ListNode<T> current = first;
        int counter = 0;
        do {
            if (current.value.equals(value)) {
                return counter;
            } else {
                counter++;
                current = current.next;
            }
        } while (counter < this.size);
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
            if (index == 0) {
                try { //in case there is only 1 element
                    this.first = this.first.next;       //let 2nd object to be 1st
                    this.first.previous = null;         //now to the first object (that was previously 2nd) set last to be null
                } catch (NullPointerException e) {
                    this.first = null;
                    this.last = null;
                }
                --size;
                ++modificationCount;
                return;
            }
            if (index == this.size - 1) {
                this.last = this.last.previous;       //let 2nd to last object to be last
                this.last.next = null;                //now to the last object (that was previously 2nd to last) set next to be null
                --size;
                ++modificationCount;
                return;
            }

            //if we are not deleting either first or last object in the list then...
            ListNode<T> current;
            int counter;

            if (index <= size / 2) {
                current = this.first;
                counter = 0;
                while (counter < index - 1) {
                    current = current.next;
                    counter++;
                }
                current.next = current.next.next;
                current.next.previous = current;

            } else {
                current = this.last;
                counter = size - 1;
                while (counter > index + 1) {
                    current = current.previous;
                    counter--;
                }

                current.previous = current.previous.previous;
                current.previous.next = current;
            }
            --size;
            ++modificationCount;
        }
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
