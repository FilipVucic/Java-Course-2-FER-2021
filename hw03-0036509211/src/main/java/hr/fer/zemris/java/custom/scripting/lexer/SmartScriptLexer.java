package hr.fer.zemris.java.custom.scripting.lexer;

import hr.fer.zemris.java.custom.scripting.tokens.SmartScriptToken;
import hr.fer.zemris.java.custom.scripting.tokens.SmartScriptTokenType;

import java.util.Objects;

/**
 * Class which represents a smart script lexer for elements of inputed text.
 *
 * @author Filip Vucic
 */
public class SmartScriptLexer {

    /**
     * Char array of input data.
     */
    private final char[] data;

    /**
     * Current {@link SmartScriptToken} of the lexer.
     */
    private SmartScriptToken token;

    /**
     * Current index.
     */
    private int currentIndex;

    /**
     * Current {@link SmartScriptLexerState} of the lexer.
     */
    private SmartScriptLexerState state = SmartScriptLexerState.TEXT;

    /**
     * Creates new {@link SmartScriptLexer} with given input text.
     *
     * @param text Input text
     */
    public SmartScriptLexer(String text) {
        Objects.requireNonNull(text, "Text can not be null!");

        data = text.toCharArray();
    }

    /**
     * Returns next {@link SmartScriptToken} of the lexer.
     *
     * @return Next {@link SmartScriptToken} of the lexer
     * @throws SmartScriptLexerException if error
     */
    public SmartScriptToken nextToken() {
        if (data.length < currentIndex) {
            throw new SmartScriptLexerException("Illegal access after EOF!");
        }

        if (data.length == currentIndex) {
            token = new SmartScriptToken(SmartScriptTokenType.EOF, null);
            currentIndex++;
            return token;
        }

        if (state.equals(SmartScriptLexerState.TAG)) {
            parseTag();
        } else if (state.equals(SmartScriptLexerState.TEXT)) {
            parseText();
        } else {
            throw new SmartScriptLexerException("Invalid lexer state!");
        }

        return token;
    }

    /**
     * Method used for parsing the text.
     */
    private void parseText() {

        StringBuilder text = new StringBuilder();
        if (data[currentIndex] == '{') {
            currentIndex++;
            if (data.length == currentIndex) {
                text.append('{');
                token = new SmartScriptToken(SmartScriptTokenType.TEXT, text.toString());
                return;
            }
            if (data[currentIndex] == '$') {
                currentIndex++;
                token = new SmartScriptToken(SmartScriptTokenType.TAGSTART, "$");
                return;
            }

            text.append('{');
        }

        while (true) {
            if (data.length == currentIndex) {
                break;
            }

            if (data[currentIndex] == '\\') {
                currentIndex++;
                if (data[currentIndex] == '\\' || data[currentIndex] == '{') {
                    text.append(data[currentIndex++]);
                    continue;
                } else {
                    throw new SmartScriptLexerException("Invalid escaping!");
                }
            }

            if (data[currentIndex] == '{') {
                currentIndex++;
                if (data.length == currentIndex) {
                    text.append('{');
                    break;
                }
                if (data[currentIndex] == '$') {
                    currentIndex--;
                    break;
                }

                text.append('{');
            } else {
                text.append(data[currentIndex++]);
            }
        }

        token = new SmartScriptToken(SmartScriptTokenType.TEXT, text.toString());
    }

    /**
     * Method used for parsing the tag.
     */
    private void parseTag() {
        while (data[currentIndex] == '\r' || data[currentIndex] == '\n'
                || data[currentIndex] == '\t' || data[currentIndex] == ' ') {
            currentIndex++;
            if (data.length == currentIndex) {
                token = new SmartScriptToken(SmartScriptTokenType.EOF, null);
                currentIndex++;
                return;
            }
        }

        try {
            if (data[currentIndex] == '$') {
                currentIndex++;

                if (data[currentIndex++] == '}') {
                    token = new SmartScriptToken(SmartScriptTokenType.TAGEND, "$");
                } else {
                    throw new SmartScriptLexerException("Bad tag!");
                }
            } else if (data[currentIndex] == '=') {
                token = new SmartScriptToken(SmartScriptTokenType.VARIABLEORTAGNAME, Character.toString(data[currentIndex++]));
            } else if (data[currentIndex] == '"') {
                StringBuilder string = new StringBuilder();
                currentIndex++;
                while (data[currentIndex] != '\"') {
                    if (data[currentIndex] == '\\') {
                        currentIndex++;
                        if (data[currentIndex] == '\\' || data[currentIndex] == '\"') {
                            string.append(data[currentIndex++]);
                        } else if (data[currentIndex] == 'n') {
                            string.append("\n");
                            currentIndex++;
                        } else if (data[currentIndex] == 'r') {
                            string.append("\r");
                            currentIndex++;
                        } else if (data[currentIndex] == 't') {
                            string.append("\t");
                            currentIndex++;
                        } else {
                            throw new SmartScriptLexerException("Invalid escaping!");
                        }
                    } else {
                        string.append(data[currentIndex++]);
                    }
                }

                currentIndex++;

                token = new SmartScriptToken(SmartScriptTokenType.STRING, string.toString());
            } else if (Character.isLetter(data[currentIndex])) {
                StringBuilder varOrTagName = new StringBuilder();
                varOrTagName.append(data[currentIndex++]);

                while (Character.isLetterOrDigit(data[currentIndex]) || data[currentIndex] == '_') {
                    varOrTagName.append(data[currentIndex++]);
                }

                token = new SmartScriptToken(SmartScriptTokenType.VARIABLEORTAGNAME, varOrTagName.toString());
            } else if (data[currentIndex] == '@') {
                currentIndex++;
                StringBuilder functionName = new StringBuilder();

                while (Character.isLetterOrDigit(data[currentIndex]) || data[currentIndex] == '_') {
                    functionName.append(data[currentIndex++]);
                }

                token = new SmartScriptToken(SmartScriptTokenType.FUNCTIONNAME, functionName.toString());
            } else if (data[currentIndex] == '+' || data[currentIndex] == '*'
                    || data[currentIndex] == '/' || data[currentIndex] == '^') {
                token = new SmartScriptToken(SmartScriptTokenType.OPERATOR, String.valueOf(data[currentIndex++]));
            } else if (Character.isDigit(data[currentIndex]) || data[currentIndex] == '-') {
                StringBuilder number = new StringBuilder();
                if (data[currentIndex] == '-') {
                    if (!Character.isDigit(data[currentIndex + 1])) {
                        token = new SmartScriptToken(SmartScriptTokenType.OPERATOR, "-");
                    } else {
                        number.append('-');
                    }
                    currentIndex++;
                }
                while (Character.isDigit(data[currentIndex]) || data[currentIndex] == '.') {
                    number.append(data[currentIndex++]);
                }

                if (number.toString().contains(".")) {
                    try {
                        double doubleNumber = Double.parseDouble(number.toString());
                        token = new SmartScriptToken(SmartScriptTokenType.DOUBLE, doubleNumber);
                    } catch (NumberFormatException ex) {
                        throw new SmartScriptLexerException("Too many '.' in double!");
                    }
                } else {
                    try {
                        int intNumber = Integer.parseInt(number.toString());
                        token = new SmartScriptToken(SmartScriptTokenType.INTEGER, intNumber);
                    } catch (NumberFormatException ex) {
                        throw new SmartScriptLexerException("Can't parse the int!");
                    }
                }
            } else {
                throw new SmartScriptLexerException("Unsupported character!");
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new SmartScriptLexerException("Tag not finished!");
        }

    }

    /**
     * Returns current {@link SmartScriptToken} of the lexer.
     * Does not generate the new one.
     *
     * @return Current {@link SmartScriptToken} of the lexer.
     */
    public SmartScriptToken getToken() {
        return token;
    }

    /**
     * Set {@link SmartScriptLexerState} of the lexer.
     *
     * @param state {@link SmartScriptLexerState} to be set
     * @throws NullPointerException if state is null
     */
    public void setState(SmartScriptLexerState state) {
        if (state == null) {
            throw new NullPointerException("State can not be null!");
        }
        this.state = state;
    }
}
