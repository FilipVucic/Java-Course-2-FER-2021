package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * Class which represents {@link SmartScriptLexer} exception and extends {@link RuntimeException}.
 *
 * @author Filip Vucic
 */
public class SmartScriptLexerException extends RuntimeException {

    /**
     * Create new {@link SmartScriptLexerException}.
     */
    public SmartScriptLexerException() {
        super();
    }

    /**
     * Create new {@link SmartScriptLexerException} with appropriate message.
     *
     * @param message Appropriate message
     */
    public SmartScriptLexerException(String message) {
        super(message);
    }
}
