package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Class which represents a {@link hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser} element as variable.
 *
 * @author Filip Vucic
 */
public class ElementVariable extends Element {

    /**
     * Variable name.
     */
    private final String name;

    /**
     * Create new {@link ElementVariable} with given variable name.
     *
     * @param name Variable name
     */
    public ElementVariable(String name) {
        this.name = name;
    }

    @Override
    public String asText() {
        return name;
    }
}
