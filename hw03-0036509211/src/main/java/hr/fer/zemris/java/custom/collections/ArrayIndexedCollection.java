package hr.fer.zemris.java.custom.collections;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

/**
 * @author Filip Vucic
 * Class which represents array indexed collection of elements and implements {@link List}.
 */
public class ArrayIndexedCollection implements List {

    /**
     * Capacity of the array if created with default constructor.
     */
    private static final int DEFAULT_CAPACITY = 16;
    /**
     * Modification count of the collection.
     */
    private long modificationCount = 0;

    /**
     * Current size of the collection.
     */
    private int size;

    /**
     * An array of object references.
     */
    private Object[] elements;

    /**
     * Creates new {@link ArrayIndexedCollection}.
     * Initializes the collection with default capacity of {@value DEFAULT_CAPACITY}.
     */
    public ArrayIndexedCollection() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Creates new {@link ArrayIndexedCollection} and initializes the collection with given capacity.
     *
     * @param initialCapacity Initial capacity of the collection
     * @throws IllegalArgumentException if given initial capacity is less than 1
     */
    public ArrayIndexedCollection(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("Initial capacity must be at least 1!");
        }

        elements = new Object[initialCapacity];
    }

    /**
     * Creates new {@link ArrayIndexedCollection} and initializes the collection with default capacity
     * of {@value DEFAULT_CAPACITY} and copies all elements from given collection to this collection.
     *
     * @param other Collection to be copied
     */
    public ArrayIndexedCollection(Collection other) {
        this(other, DEFAULT_CAPACITY);
    }

    /**
     * Creates new {@link ArrayIndexedCollection} and  initializes the collection with given capacity
     * and copies all elements from given collection to this collection.
     * If the initial capacity is smaller than given collection's size, the main collection's initial capacity
     * will be size of the given collection.
     *
     * @param other           Collection to be copied
     * @param initialCapacity Initial capacity
     */
    public ArrayIndexedCollection(Collection other, int initialCapacity) {
        this(initialCapacity);

        if (other == null) {
            throw new NullPointerException("Given collection shouldn't be null!");
        }

        if (initialCapacity < other.size()) {
            reallocateArray(other.size());
        }

        this.addAll(other);
    }

    @Override
    public int size() {
        return this.size;
    }

    /**
     * Average complexity O(1).
     *
     * @param value Value to be added
     */
    @Override
    public void add(Object value) {
        insert(value, this.size);
    }

    @Override
    public boolean contains(Object value) {
        return indexOf(value) != -1;
    }

    @Override
    public boolean remove(Object value) {
        int indexOfValue = indexOf(value);
        if (indexOfValue == -1) {
            return false;
        }

        remove(indexOfValue);
        return true;
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(elements, this.size);
    }

    @Override
    public void clear() {
        for (int i = 0; i < this.size; i++) {
            elements[i] = null;
        }

        this.size = 0;
        modificationCount++;
    }

    @Override
    public ElementsGetter createElementsGetter() {
        return new ArrayIndexedCollectionElementsGetter(this);
    }

    @Override
    public Object get(int index) {
        if (index < 0 || index > this.size - 1) {
            throw new IndexOutOfBoundsException("Index out of collection's bounds!");
        }

        return elements[index];
    }

    @Override
    public void insert(Object value, int position) {
        if (value == null) {
            throw new NullPointerException("Value should not be null!");
        }

        if (position < 0 || position > this.size) {
            throw new IndexOutOfBoundsException("Index out of collection's bounds!");
        }

        if (this.size == elements.length) {
            reallocateArray(this.size * 2);
        }

        for (int i = this.size; i > position; i--) {
            elements[i] = elements[i - 1];
        }

        elements[position] = value;
        this.size++;
        modificationCount++;
    }

    @Override
    public int indexOf(Object value) {
        for (int i = 0; i < this.size; i++) {
            if (elements[i].equals(value)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void remove(int index) {
        if (index < 0 || index > this.size - 1) {
            throw new IndexOutOfBoundsException("Index out of collection's bounds!");
        }

        for (int i = index; i < this.size - 1; i++) {
            elements[i] = elements[i + 1];
        }

        elements[this.size - 1] = null;
        this.size--;
        modificationCount++;
    }

    /**
     * Private method used for reallocating the elements array with bigger capacity.
     *
     * @param newCapacity New capacity of the elements array
     * @throws IllegalArgumentException if new capacity is smaller than the old one
     */
    private void reallocateArray(int newCapacity) {
        elements = Arrays.copyOf(elements, newCapacity);
    }

    /**
     * @author Filip Vucic
     * Class which represents {@link ElementsGetter} of the {@link ArrayIndexedCollection}.
     */
    private static class ArrayIndexedCollectionElementsGetter implements ElementsGetter {

        /**
         * Saved modification count of the {@link ArrayIndexedCollection} reference.
         */
        private final long savedModificationCount;

        /**
         * Reference to this {@link ArrayIndexedCollection}.
         */
        private final ArrayIndexedCollection reference;

        /**
         * Current index.
         */
        private int currentIndex = 0;

        /**
         * Create new {@link ArrayIndexedCollectionElementsGetter} with given {@link ArrayIndexedCollection} reference.
         *
         * @param reference Reference to this {@link ArrayIndexedCollection}
         */
        public ArrayIndexedCollectionElementsGetter(ArrayIndexedCollection reference) {
            this.reference = reference;
            savedModificationCount = reference.modificationCount;
        }

        @Override
        public boolean hasNextElement() {
            if (reference.modificationCount != savedModificationCount) {
                throw new ConcurrentModificationException("Collection has been modified!");
            }

            return currentIndex < reference.size;
        }

        @Override
        public Object getNextElement() {
            if (hasNextElement()) {
                return reference.get(currentIndex++);
            } else {
                throw new NoSuchElementException("No more elements in the collection!");
            }
        }
    }
}
