package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Class which represents a {@link hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser} element as constant double.
 *
 * @author Filip Vucic
 */
public class ElementConstantDouble extends Element {

    /**
     * Constant double value.
     */
    private final double value;

    /**
     * Create new {@link ElementConstantDouble} with double value.
     *
     * @param value Double value
     */
    public ElementConstantDouble(double value) {
        this.value = value;
    }

    @Override
    public String asText() {
        return Double.toString(value);
    }
}
