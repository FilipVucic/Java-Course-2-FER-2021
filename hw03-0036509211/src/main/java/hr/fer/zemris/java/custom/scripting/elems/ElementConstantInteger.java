package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Class which represents a {@link hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser} element as constant integer.
 *
 * @author Filip Vucic
 */
public class ElementConstantInteger extends Element {

    /**
     * Constant int value.
     */
    private final int value;

    /**
     * Create new {@link ElementConstantInteger} with int value.
     *
     * @param value Int value
     */
    public ElementConstantInteger(int value) {
        this.value = value;
    }

    @Override
    public String asText() {
        return Integer.toString(value);
    }
}
