package hr.fer.zemris.java.custom.collections;

/**
 * @author Filip Vucic
 * Interface which represents getter of elements in some collection.
 */
public interface ElementsGetter {

    /**
     * Checks if collection has next element.
     *
     * @return True if has next element, false otherwise
     */
    boolean hasNextElement();

    /**
     * Returns next element from the collection.
     *
     * @return Next element from the collection
     */
    Object getNextElement();

    /**
     * Process remaining elements left in the {@link ElementsGetter} with the given processor.
     *
     * @param p Processor to process the remaining elements
     */
    default void processRemaining(Processor p) {
        while (hasNextElement()) {
            p.process(getNextElement());
        }
    }
}
