package hr.fer.zemris.java.custom.scripting.tokens;

/**
 * Enum which contains {@link SmartScriptToken} types.
 *
 * @author Filip Vucic
 */
public enum SmartScriptTokenType {

    /**
     * Text token type.
     */
    TEXT,

    /**
     * Variable or tag name token type.
     * Starts with letter, and continues with letters, digits or underscores.
     */
    VARIABLEORTAGNAME,

    /**
     * Function name token type. Starts with '@'.
     */
    FUNCTIONNAME,

    /**
     * Operator token type.
     */
    OPERATOR,

    /**
     * Integer token type.
     */
    INTEGER,

    /**
     * Double token type.
     */
    DOUBLE,

    /**
     * String token type.
     */
    STRING,

    /**
     * Tag start token type.
     */
    TAGSTART,

    /**
     * Tag end token type.
     */
    TAGEND,

    /**
     * End of file token type.
     */
    EOF
}
