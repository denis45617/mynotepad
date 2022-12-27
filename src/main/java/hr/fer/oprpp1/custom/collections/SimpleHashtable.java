package hr.fer.oprpp1.custom.collections;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Custom HashTable class
 *
 * @param <K> type for key
 * @param <V> type for value
 */
public class SimpleHashtable<K, V> implements Iterable<SimpleHashtable.TableEntry<K, V>> {
    private TableEntry<K, V>[] hashTable;
    private int size;
    private int modificationCount;

    /**
     * Public constructor for SimpleHashtable
     */
    public SimpleHashtable() {
        this(16);
    }

    /**
     * Public constructor for SimpleHashtable - takes one parameter - initial capacity
     *
     * @param capacity capacity of HashTable
     * @throws IllegalArgumentException thrown when capacity is null;
     */
    @SuppressWarnings("unchecked")
    public SimpleHashtable(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("Capacity can't be lower than 1");
        int hashTableCapacity = (capacity == 1) ? 1 : (int) Math.pow(2, 1 + (int) (Math.log(capacity - 1) / Math.log(2)));
        hashTable = new TableEntry[hashTableCapacity];
        this.size = 0;
        this.modificationCount = 0;
    }

    @Override
    public Iterator<SimpleHashtable.TableEntry<K, V>> iterator() {
        return new IteratorImpl();
    }

    /**
     * Custom Iterator class. Allows removing elements
     */
    private class IteratorImpl implements Iterator<SimpleHashtable.TableEntry<K, V>> {
        /**
         * Ovdje pamtim trenutni red
         */
        private int hashTableRowIndex;
        /**
         * Ovdje pamtim trenutni node u redu
         */
        private TableEntry<K, V> currentInsideRow;
        /**
         * Counter govori koliko elemenata sam već prošao. //potreba diskutabilna
         */
        private int counter;
        /**
         * Zastavica s kojom pazim da ne idem dva puta remove preko iteratora
         */
        private boolean currentIsRemoved = false;
        /**
         * spremljen broj modifikacija
         */
        private int savedModificationCount;

        /**
         * Konstruktor za iterator
         */
        private IteratorImpl() {
            this.hashTableRowIndex = 0;
            this.currentInsideRow = null;
            this.counter = 0;
            this.savedModificationCount = modificationCount;
        }


        @Override //navedeno eksplicitno zbog javadoc
        public boolean hasNext() {
            if (savedModificationCount != modificationCount)
                throw new ConcurrentModificationException("Hashtable has been changed while iterating");
            return counter < size;
        }

        @Override //navedeno eksplicitno zbog javadoc
        public SimpleHashtable.TableEntry<K, V> next() {
            currentIsRemoved = false;
            if (this.hasNext()) {  //pomaknem se i ispišem

                if (currentInsideRow == null) {
                    while (hashTableRowIndex < hashTable.length && hashTable[hashTableRowIndex] == null) { //vidi jesam li u redu u kojem ima  nešta
                        hashTableRowIndex++;
                    }
                    currentInsideRow = hashTable[hashTableRowIndex];
                    counter++;
                    return currentInsideRow;

                }
                if (currentInsideRow.next != null) {
                    currentInsideRow = currentInsideRow.next;
                    counter++;
                    return currentInsideRow;
                } else {
                    currentInsideRow = null;
                    hashTableRowIndex++;
                    return next();    //wow rekurzija :POG:
                }
            } else {    //ako je false za hasNext baci exception
                throw new NoSuchElementException();
            }

        }

        @Override //navedeno eksplicitno zbog javadoc
        public void remove() {
            if (currentIsRemoved)
                throw new IllegalStateException("You can only remove 1 parameter at once with iterator");

            modificationCount++;    //ova dva se povećavaju zbog mogućih drugih iteratora. Za ovaj iterator međusobno oni ostaju isti. #...misliNaDruge
            savedModificationCount++;

            currentIsRemoved = true;
            size--;
            counter--; //30 minuta right here
            TableEntry<K, V> current = hashTable[hashTableRowIndex];


            if (current.equals(currentInsideRow)) {             //ako je prvi
                if (current.next != null) {                     //ako postoji drugi
                    hashTable[hashTableRowIndex] = currentInsideRow.next; //neka drugi bude sada prvi
                    currentInsideRow = null;
                } else {                                    //ako ne postoji drugi
                    hashTable[hashTableRowIndex] = null; //postavi tu liniju tablica na null
                    currentInsideRow = null;            //pokazivač na trenutni postavi na null
                    hashTableRowIndex++;                //odi u sljedeći red
                }
            } else { //ako nije prvi
                while (!current.next.equals(currentInsideRow)) {
                    current = current.next;
                }
                current.next = current.next.next;
                currentInsideRow = currentInsideRow.next;
                if (currentInsideRow.next == null) {
                    currentInsideRow = null;            //pokazivač na trenutni postavi na null
                    hashTableRowIndex++;                //odi u sljedeći red
                }
            }
        }

    }


    /**
     * TableEntry node
     *
     * @param <K> key type
     * @param <V> value type
     */
    public static class TableEntry<K, V> {
        private K key;
        private V value;
        private TableEntry<K, V> next;

        /**
         * Public getter for TableEntryKey
         *
         * @return K key
         */
        public K getKey() {
            return key;
        }

        /**
         * Public getter for TableEnty value
         *
         * @return V value
         */
        public V getValue() {
            return value;
        }

        /**
         * Public setter for TableEntry value
         *
         * @param value value
         */
        public void setValue(V value) {
            this.value = value;
        }

        public TableEntry(K key, V value, TableEntry<K, V> next) {
            if (key == null) throw new NullPointerException("Ključ ne može biti null");
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    /**
     * Inserts Key-value pair into the SimpleHashtable. If there was already key-value pair with the given key, overwrite
     * its value. Otherwise, just add key-value pair to the table
     *
     * @param key   key
     * @param value value
     * @return null if given key was already not in the table, otherwise return old value for the given key
     */

    public V put(K key, V value) {
        if (key == null) throw new NullPointerException("Ključ ne može biti null!");

        if (size * 100.0 / hashTable.length >= 75) {
            reallocate();
        }

        return addInHashTable(hashTable, key, value);
    }

    /**
     * Helper method for avoiding redundant code. Adds key and value in given hashTable
     *
     * @param hashTable hashTable in which adding key-value pair
     * @param key       key
     * @param value     value
     * @return overwritten value if key was already in hashTable, null if key-value pair is just added
     */
    private V addInHashTable(TableEntry<K, V>[] hashTable, K key, V value) {
        int tableEntryIndex = Math.abs(key.hashCode()) % hashTable.length;
        if (hashTable[tableEntryIndex] == null) {
            hashTable[tableEntryIndex] = new TableEntry<>(key, value, null);
        } else {
            TableEntry<K, V> current = hashTable[tableEntryIndex];
            while (current.next != null && !current.key.equals(key)) { //idi do kraja ili do .. sa istim ključem
                current = current.next;
            }

            if (current.key.equals(key)) {          //ako je isti ključ prepiši vrijednost i vrati staru
                V valueToBeReturned = current.value;
                current.value = value;
                return valueToBeReturned;       //zbog return ne povećavam modificationCount što je dobro
            }
            current.next = new TableEntry<>(key, value, null);

        }
        modificationCount++;
        this.size++;
        return null;
    }

    /**
     * Helper method for reallocating hashTable. (Making it twice the size)
     */
    @SuppressWarnings("unchecked")
    private void reallocate() {
        TableEntry<K, V>[] hashTable2 = (TableEntry<K, V>[]) new TableEntry[2 * hashTable.length];
        for (TableEntry<K, V> kvTableEntry : hashTable) {
            if (kvTableEntry != null) {
                TableEntry<K, V> current = kvTableEntry;
                while (current != null) {
                    addInHashTable(hashTable2, current.key, current.value);
                    size--; //jer ga addInHashTable poveća
                    current = current.next;
                }
            }
        }
        hashTable = hashTable2;
    }

    /**
     * Method that gets value for the given Key. If there was no value for the given key - null is returned
     *
     * @param key key
     * @return value for the given key
     */
    public V get(K key) {
        //Metoda get pozvana s ključem koji u tablici ne postoji vraća null
        if (key == null) return null;

        //odrediti mjestu u tablici na kojem treba tražiti
        int tableEntryIndex = Math.abs(key.hashCode()) % hashTable.length;
        if (!(hashTable[tableEntryIndex] == null)) {                //ako nešta ima na toj poziciji u hashtable
            TableEntry<K, V> current = hashTable[tableEntryIndex];
            while (current.next != null && !current.key.equals(key)) { //idi do kraja ili do .. sa istim ključem
                current = current.next;
            }

            if (current.key.equals(key)) {          //ako je isti ključ pronađen,
                return current.value;              //vrati value za njega
            }
        }

        return null;

    }

    /**
     * Returns the size of the HashTable - number of elements in it
     *
     * @return size of the hashTable
     */
    public int size() {
        return this.size;
    }

    /**
     * Method that checks if HashTable contains given key
     *
     * @param key given key
     * @return true if key is contained and vice versa
     */
    public boolean containsKey(Object key) {
        if (key == null) return false;

        int tableEntryIndex = Math.abs(key.hashCode()) % hashTable.length;
        if (!(hashTable[tableEntryIndex] == null)) {                //ako nešta ima na toj poziciji u hashtable
            TableEntry<K, V> current = hashTable[tableEntryIndex];
            while (current.next != null && !current.key.equals(key)) { //idi do kraja ili do .. sa istim ključem
                current = current.next;
            }
            //ako je isti ključ pronađen,
            return current.key.equals(key);
        }

        return false;
    }


    /**
     * Removes key-value pair from hashtable for the given key. If given parameter is null, method does nothing
     * If given parameter is not key that is contained in the table it does nothing.
     *
     * @param key key
     * @return null if parameter is null or given key is not contained in the table, true otherwise
     * (key-value pair is removed)
     */
    public V remove(Object key) {
        if (key == null) return null;

        int tableEntryIndex = Math.abs(key.hashCode()) % hashTable.length;
        if (!(hashTable[tableEntryIndex] == null)) {                //ako nešta ima na toj poziciji u hashtable
            TableEntry<K, V> current = hashTable[tableEntryIndex];
            while (current.next != null && !current.next.key.equals(key) && !current.key.equals(key)) { //idi do kraja ili do .. sa istim ključem
                current = current.next;
            }

            if (current.key.equals(key)) {  //ako je prvi element ima baš taj ključ
                V valueToBeReturned = current.value;
                hashTable[tableEntryIndex] = current.next;
                size--;
                modificationCount++;
                return valueToBeReturned;
            }

            if (current.next == null) { //nema takvog ključa u tablici
                return null;
            }

            if (current.next.key.equals(key)) {   //ima takvog ključa, prespoji
                V valueToBeReturned = current.next.value;
                current.next = current.next.next;
                size--;
                modificationCount++;
                return valueToBeReturned;
            }
        }
        return null;

    }

    /**
     * Methods that checks if HashTable is Empty
     *
     * @return true if empty, false if there are elements in it
     */
    public boolean isEmpty() {
        return size == 0;
    }

    public boolean containsValue(Object value) {
        for (TableEntry<K, V> kvTableEntry : hashTable) {
            if (kvTableEntry != null) {
                TableEntry<K, V> current = kvTableEntry;
                while (current.next != null && !current.value.equals(value)) {
                    current = current.next;
                }

                if (value == null && current.value == null) {
                    return true;
                }

                if (current.value != null && current.value.equals(value))
                    return true;
            }
        }

        return false;
    }

    /**
     * Returns String representation of hashTable  [key1=value1, key2=value2,... keyN=valueN]
     *
     * @return String value
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (TableEntry<K, V> kvTableEntry : hashTable) {
            if (kvTableEntry != null) {
                TableEntry<K, V> current = kvTableEntry;
                while (current != null) {
                    sb.append(current.key).append("=").append(current.value).append(", ");
                    current = current.next;
                }
            }
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append("]");
        return sb.toString();
    }


    /**
     * Returns Array of pointers to TableEntries
     *
     * @return
     */
    @SuppressWarnings("unchecked") //#jaProgramer
    public TableEntry<K, V>[] toArray() {
        TableEntry<K, V>[] polje = (TableEntry<K, V>[]) new TableEntry[size];
        int counter = 0;

        for (TableEntry<K, V> kvTableEntry : hashTable) {
            if (kvTableEntry != null) {
                TableEntry<K, V> current = kvTableEntry;
                while (current != null) {
                    polje[counter++] = current;
                    current = current.next;
                }
            }
        }

        return polje;
    }

    /**
     * Method that removes all elements from the hashTable, doesn't touch the structure of it. (Capacity stays the same)
     */
    public void clear() {
        for (int i = 0; i < hashTable.length; ++i) {
            hashTable[i] = null;
        }
        size = 0;
        modificationCount++;
    }

}
