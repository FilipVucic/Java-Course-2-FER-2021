package hr.fer.zemris.java.custom.collections;

/**
 * @author Filip Vucic
 * Interface which represents some general collection of objects.
 */
public interface Collection {

    /**
     * Checks if collection is empty.
     *
     * @return True if collection is empty, false otherwise
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns the size of the collection.
     *
     * @return Size of the collection
     */
    int size();

    /**
     * Add value to the collection.
     *
     * @param value Value to be added
     */
    void add(Object value);

    /**
     * Check if the collection contains given value.
     *
     * @param value Value to be checked
     * @return True if the collection contains given value, false otherwise
     */
    boolean contains(Object value);

    /**
     * Remove one occurrence of value from the collection.
     *
     * @param value Value to be removed
     * @return True if the collection contains given value, false otherwise
     */
    boolean remove(Object value);

    /**
     * Return array with collection content.
     *
     * @return Array with collection content
     */
    Object[] toArray();

    /**
     * Call {@link Processor}.process for each element of this collection.
     *
     * @param processor Collection processor
     */
    default void forEach(Processor processor) {
        ElementsGetter getter = this.createElementsGetter();

        while (getter.hasNextElement()) {
            processor.process(getter.getNextElement());
        }
    }

    /**
     * Add into current collection all elements from other collection.
     * Other collection remains unchanged.
     *
     * @param other Collection whose elements will be added
     */
    default void addAll(Collection other) {
        class AddProcessor implements Processor {
            @Override
            public void process(Object value) {
                add(value);
            }
        }

        other.forEach(new AddProcessor());
    }

    /**
     * Remove all elements from the collection.
     */
    void clear();

    /**
     * Create new {@link ElementsGetter} of this collection.
     *
     * @return New {@link ElementsGetter}
     */
    ElementsGetter createElementsGetter();

    /**
     * Add all satisfying elements from other collection to this collection. Elements are
     * satisfying if they pass the test from the given tester.
     *
     * @param col    Collection with elements to be added
     * @param tester Tester which will test the elements
     */
    default void addAllSatisfying(Collection col, Tester tester) {
        ElementsGetter getter = col.createElementsGetter();

        while (getter.hasNextElement()) {
            Object nextElement = getter.getNextElement();
            if (tester.test(nextElement)) {
                this.add(nextElement);
            }
        }
    }
}
