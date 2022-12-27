package hr.fer.oprpp1.custom.collections;

/**
 * Public class Dictionary is custom Map implementation
 *
 * @param <K> Type for key
 * @param <V> Type for value
 */
public class Dictionary<K, V> {
    private ArrayIndexedCollection<EntrySet<K, V>> dictionary;

    /**
     * EntrySet nested class is a structure that has Key and Value. Where key can not be null
     *
     * @param <K> key type
     * @param <V> value type
     */
    private static class EntrySet<K, V> {
        K key;
        V value;

        /**
         * Constructor for  EntrySet, takes K key and V value.
         *
         * @param key   key
         * @param value value
         * @throws NullPointerException thrown when given key is null
         */
        private EntrySet(K key, V value) {
            if (key == null) {
                throw new NullPointerException("Key ne smije biti null");
            }
            this.key = key;
            this.value = value;
        }

        private K getKey() {
            return key;
        }

        private V getValue() {
            return value;
        }

        private void setValue(V value) {
            this.value = value;
        }
    }

    /**
     * Public constructor for dictionary
     */
    public Dictionary() {
        dictionary = new ArrayIndexedCollection<>();
    }

    /**
     * Checks if dictionary is empty and returns true/fals
     *
     * @return boolean; true if dictionary is empty, false - there is 1 or more elements in it
     */
    boolean isEmpty() {
        return dictionary.isEmpty();
    }

    /**
     * Returns size of the dictionary
     *
     * @return int size of the dictionary
     */
    int size() {
        return dictionary.size();
    }

    /**
     * Removes all Key - Value pairs from dictionary
     */
    void clear() {
        dictionary.clear();
    }


    /**
     * Adds key-value pair to the dictionary. If there is already key-value pair with the given key, it changes it value
     * Duplicate key in same dictionary is not possible.
     *
     * @param key   key
     * @param value value
     * @return null if key-value pair with the given key wasn't already in the list, otherwise older value
     */
    V put(K key, V value) { // "gazi" eventualni postojeći zapis
        if (key == null) throw new NullPointerException("Key ne može biti null!");
        V valueToToBeReturned = null;
        int index = indexOfKeyPair(key);
        if (index == -1) {
            dictionary.add(new EntrySet<>(key, value));
        } else {
            valueToToBeReturned = dictionary.get(index).getValue();
            dictionary.get(index).setValue(value);
        }
        return valueToToBeReturned;
    }

    /**
     * Returns value for the given key. If there is no key-value pair for the given key then it returns null
     *
     * @param key key
     * @return value for the given key, null if no pair with given key in dictionary found
     */
    V get(Object key) {                              // ako ne postoji pripadni value, vraća null
        int index = indexOfKeyPair(key);            //nađi index para koji ima taj key
        if (index == -1) {                         //ako ga nema
            return null;                          //vrati null
        }
        return dictionary.get(index).getValue();    //inače vrati njegov value

    }


    /**
     * Removes key-value pair for the given key
     *
     * @param key key
     * @return value of the removed key-value pair
     */

    V remove(K key) {
        V valueToBeReturned = null;
        int indexOfTheKeyValuePair = indexOfKeyPair(key);  //get index of the entryset that has given key

        if (indexOfTheKeyValuePair != -1) {                 //if there was one entryset with the given key
            valueToBeReturned = this.get(key);              //get it's value
            dictionary.remove(indexOfTheKeyValuePair);      //and remove it from the dictionary
        }

        return valueToBeReturned;
    }


    /**
     * Helper method that returns index of key-value pair for the given key
     *
     * @param key key
     * @return index of key-value pair
     */
    private int indexOfKeyPair(Object key) {
        if (key == null) return -1;

        int i = 0;
        for (; i < dictionary.size(); ++i) {
            if (dictionary.get(i).getKey().equals(key))
                return i;
        }
        return -1;
    }

}
