package hr.fer.zemris.java.custom.scripting.parser;

/**
 * Class which represents {@link SmartScriptParser} exception and extends {@link RuntimeException}.
 *
 * @author Filip Vucic
 */
public class SmartScriptParserException extends RuntimeException {

    /**
     * Create new {@link SmartScriptParserException}.
     */
    public SmartScriptParserException() {
    }

    /**
     * Create new {@link SmartScriptParserException} with appropriate message.
     *
     * @param message Appropriate message
     */
    public SmartScriptParserException(String message) {
        super(message);
    }
}
