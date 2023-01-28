package hr.fer.zemris.java.custom.collections;

/**
 * @author Filip Vucic
 * Interface which represents a List.
 */
public interface List extends Collection {

    /**
     * Get collection's element at the given index.
     *
     * @param index Element index
     * @return Object that is stored in backing array at position index
     */
    Object get(int index);

    /**
     * Insert element in the collection at the given position.
     *
     * @param value    Value to be inserted
     * @param position Position of value in the collection
     */
    void insert(Object value, int position);

    /**
     * Get given element's index in the collection.
     *
     * @param value Value whose index is searched for
     * @return Index of the given element, -1 if given element is not found in the collection
     */
    int indexOf(Object value);

    /**
     * Remove element from the collection at the given index.
     *
     * @param index Index of the element to be removed
     */
    void remove(int index);
}
