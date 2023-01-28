package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Class which represents a {@link hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser} element as function.
 *
 * @author Filip Vucic
 */
public class ElementFunction extends Element {

    /**
     * Name of the function.
     */
    private final String name;

    /**
     * Create new {@link ElementFunction} with function name.
     *
     * @param name Function name
     */
    public ElementFunction(String name) {
        this.name = name;
    }

    @Override
    public String asText() {
        return "@" + name;
    }
}
