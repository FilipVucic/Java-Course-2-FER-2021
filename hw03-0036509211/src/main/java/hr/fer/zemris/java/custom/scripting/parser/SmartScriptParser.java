package hr.fer.zemris.java.custom.scripting.parser;

import hr.fer.zemris.java.custom.collections.ArrayIndexedCollection;
import hr.fer.zemris.java.custom.collections.EmptyStackException;
import hr.fer.zemris.java.custom.collections.ObjectStack;
import hr.fer.zemris.java.custom.scripting.elems.*;
import hr.fer.zemris.java.custom.scripting.lexer.SmartScriptLexer;
import hr.fer.zemris.java.custom.scripting.lexer.SmartScriptLexerException;
import hr.fer.zemris.java.custom.scripting.lexer.SmartScriptLexerState;
import hr.fer.zemris.java.custom.scripting.nodes.*;
import hr.fer.zemris.java.custom.scripting.tokens.SmartScriptToken;
import hr.fer.zemris.java.custom.scripting.tokens.SmartScriptTokenType;

/**
 * Class which represents parser of {@link SmartScriptLexer} nodes.
 *
 * @author Filip Vucic
 */
public class SmartScriptParser {

    /**
     * Stack as storage of nodes.
     */
    private final ObjectStack stack;

    /**
     * {@link SmartScriptLexer} for nodes.
     */
    private final SmartScriptLexer lexer;

    /**
     * Main node.
     */
    private final DocumentNode documentNode;

    /**
     * Create new {@link SmartScriptParser} with given document body to be parsed.
     *
     * @param docBody Document body to be parsed
     */
    public SmartScriptParser(String docBody) {
        stack = new ObjectStack();
        lexer = new SmartScriptLexer(docBody);
        documentNode = new DocumentNode();
        stack.push(documentNode);
        parse();
    }

    /**
     * Get document node.
     *
     * @return Document node
     */
    public DocumentNode getDocumentNode() {
        return documentNode;
    }

    /**
     * Parse document body.
     */
    private void parse() {
        while (true) {
            SmartScriptToken token;
            try {
                token = lexer.nextToken();
            } catch (SmartScriptLexerException ex) {
                throw new SmartScriptParserException("Lexer exception: " + ex.getMessage());
            }


            if (token.getType().equals(SmartScriptTokenType.EOF)) {
                break;
            } else if (token.getType().equals(SmartScriptTokenType.TAGSTART)) {
                parseTag();
            } else {
                parseText(token.getValue());
            }
        }

        if (stack.size() > 1) {
            throw new SmartScriptParserException("You have unclosed FOR loops!");
        }

        if (stack.size() < 1) {
            throw new SmartScriptParserException("Too many END tags!");
        }

    }

    /**
     * Parse text.
     *
     * @param tokenValue Text value
     */
    private void parseText(Object tokenValue) {
        TextNode textNode = new TextNode((String) tokenValue);
        Node topNode;
        try {
            topNode = (Node) stack.peek();
        } catch (EmptyStackException ex) {
            throw new SmartScriptParserException("Too many END tags!");
        }
        topNode.addChildNode(textNode);

    }

    /**
     * Parse tag.
     */
    private void parseTag() {
        lexer.setState(SmartScriptLexerState.TAG);

        SmartScriptToken token;
        try {
            token = lexer.nextToken();
        } catch (SmartScriptLexerException ex) {
            throw new SmartScriptParserException("Lexer exception: " + ex.getMessage());
        }

        if (!token.getType().equals(SmartScriptTokenType.VARIABLEORTAGNAME)) {
            throw new SmartScriptParserException("Tag must begin with tag name!");
        }

        String tagName = (String) token.getValue();

        if (tagName.equalsIgnoreCase("FOR")) {
            parseForLoop();
        } else if (tagName.equalsIgnoreCase("END")) {
            parseEnd();
        } else if (tagName.equals("=")) {
            parseEcho();
        } else {
            throw new SmartScriptParserException("Invalid tag name!");
        }

        lexer.setState(SmartScriptLexerState.TEXT);
    }

    /**
     * Parse for loop inside tag.
     */
    private void parseForLoop() {
        SmartScriptToken token;
        try {
            token = lexer.nextToken();
        } catch (SmartScriptLexerException ex) {
            throw new SmartScriptParserException("Lexer exception: " + ex.getMessage());
        }

        if (!token.getType().equals(SmartScriptTokenType.VARIABLEORTAGNAME)) {
            throw new SmartScriptParserException("FOR tag must begin with variable name!");
        }
        ElementVariable variable = (ElementVariable) tokenToElement(token);
        Element[] forLoopElements = new Element[3];

        for (int i = 0; i < 3; i++) {
            try {
                token = lexer.nextToken();
            } catch (SmartScriptLexerException ex) {
                throw new SmartScriptParserException("Lexer exception: " + ex.getMessage());
            }
            if (token.getType().equals(SmartScriptTokenType.OPERATOR) || token.getType().equals(SmartScriptTokenType.FUNCTIONNAME)) {
                throw new SmartScriptParserException("FOR tag must contain only variables, numbers or strings!");
            }
            if (token.getType().equals(SmartScriptTokenType.TAGEND)) {
                if (i == 2) {
                    break;
                } else {
                    throw new SmartScriptParserException("FOR loop too short!");
                }
            }

            forLoopElements[i] = tokenToElement(token);
            if (i == 2) {
                try {
                    lexer.nextToken();
                } catch (SmartScriptLexerException ex) {
                    throw new SmartScriptParserException("Lexer exception: " + ex.getMessage());
                }
            }
        }

        ForLoopNode forLoopNode = new ForLoopNode(variable, forLoopElements[0], forLoopElements[1], forLoopElements[2]);

        Node topNode;
        try {
            topNode = (Node) stack.peek();
        } catch (EmptyStackException ex) {
            throw new SmartScriptParserException("Too many END tags!");
        }

        topNode.addChildNode(forLoopNode);
        stack.push(forLoopNode);
    }

    /**
     * Parse echo inside tag.
     */
    private void parseEcho() {
        ArrayIndexedCollection elementsList = new ArrayIndexedCollection();
        SmartScriptToken token;
        try {
            token = lexer.nextToken();
        } catch (SmartScriptLexerException ex) {
            throw new SmartScriptParserException("Lexer exception: " + ex.getMessage());
        }

        if (token.getType().equals(SmartScriptTokenType.TAGEND)) {
            throw new SmartScriptParserException("= tag can not be empty!");
        }

        while (!token.getType().equals(SmartScriptTokenType.TAGEND)) {
            elementsList.add(tokenToElement(token));

            try {
                token = lexer.nextToken();
            } catch (SmartScriptLexerException ex) {
                throw new SmartScriptParserException("Lexer exception: " + ex.getMessage());
            }
        }

        Element[] elements = new Element[elementsList.size()];

        for (int i = 0; i < elementsList.size(); i++) {
            elements[i] = (Element) elementsList.get(i);
        }

        EchoNode echoNode = new EchoNode(elements);

        Node topNode;
        try {
            topNode = (Node) stack.peek();
        } catch (EmptyStackException ex) {
            throw new SmartScriptParserException("Too many END tags!");
        }

        topNode.addChildNode(echoNode);
    }

    /**
     * Parse {@link SmartScriptToken} to {@link Element}.
     *
     * @param token Token to be parsed
     * @return Parsed element
     */
    private Element tokenToElement(SmartScriptToken token) {
        Object tokenValue = token.getValue();
        SmartScriptTokenType tokenType = token.getType();
        Element returnElement;
        if (tokenType.equals(SmartScriptTokenType.VARIABLEORTAGNAME)) {
            returnElement = new ElementVariable((String) tokenValue);
        } else if (tokenType.equals(SmartScriptTokenType.STRING)) {
            returnElement = new ElementString((String) tokenValue);
        } else if (tokenType.equals(SmartScriptTokenType.DOUBLE)) {
            returnElement = new ElementConstantDouble((double) tokenValue);
        } else if (tokenType.equals(SmartScriptTokenType.INTEGER)) {
            returnElement = new ElementConstantInteger((int) tokenValue);
        } else if (tokenType.equals(SmartScriptTokenType.OPERATOR)) {
            returnElement = new ElementOperator((String) tokenValue);
        } else if (tokenType.equals(SmartScriptTokenType.FUNCTIONNAME)) {
            returnElement = new ElementFunction((String) tokenValue);
        } else {
            throw new SmartScriptParserException("Invalid token type!");
        }

        return returnElement;
    }

    /**
     * Parse end inside tag.
     */
    private void parseEnd() {
        SmartScriptToken token;
        try {
            token = lexer.nextToken();
        } catch (SmartScriptLexerException ex) {
            throw new SmartScriptParserException("Lexer exception: " + ex.getMessage());
        }

        if (!token.getType().equals(SmartScriptTokenType.TAGEND)) {
            throw new SmartScriptParserException("END tag must contain only tag name!");
        }

        try {
            stack.pop();
        } catch (EmptyStackException ex) {
            throw new SmartScriptParserException("Too many END tags!");
        }
    }
}
