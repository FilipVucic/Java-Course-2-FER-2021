package hr.fer.zemris.java.custom.collections;

/**
 * @author Filip Vucic
 * Class Processor here represents an conceptual contract between clients
 * which will have objects to be processed, and each concrete Processor
 * which knows how to perform the selected operation.
 */
public interface Processor {
    /**
     * Process an object.
     *
     * @param value Object to be processed
     */
    void process(Object value);
}
