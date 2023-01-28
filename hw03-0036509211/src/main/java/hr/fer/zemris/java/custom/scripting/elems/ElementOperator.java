package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Class which represents a {@link hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser} element as operator.
 *
 * @author Filip Vucic
 */
public class ElementOperator extends Element {

    /**
     * Operator symbol.
     */
    private final String symbol;

    /**
     * Create new {@link ElementOperator} with given symbol.
     *
     * @param symbol Symbol
     */
    public ElementOperator(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String asText() {
        return symbol;
    }
}
