package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Class which represents a {@link hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser} element as string.
 *
 * @author Filip Vucic
 */
public class ElementString extends Element {

    /**
     * Value of the string.
     */
    private final String value;

    /**
     * Create new {@link ElementString} with given string value.
     *
     * @param value String value
     */
    public ElementString(String value) {
        this.value = value;
    }

    @Override
    public String asText() {
        return value;
    }
}
