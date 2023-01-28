package hr.fer.zemris.java.custom.scripting.nodes;


import hr.fer.zemris.java.custom.collections.ArrayIndexedCollection;
import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;

import java.util.Objects;

/**
 * Class which represents for loop node and extends {@link Node}.
 *
 * @author Filip Vucic
 */
public class ForLoopNode extends Node {

    /**
     * Variable of the for loop.
     */
    private final ElementVariable variable;

    /**
     * Start expression of the for loop.
     */
    private final Element startExpression;

    /**
     * End expression of the for loop.
     */
    private final Element endExpression;

    /**
     * Step expression of the for loop.
     * Can be null.
     */
    private final Element stepExpression;

    /**
     * {@link ForLoopNode} children.
     */
    private ArrayIndexedCollection children;

    /**
     * Create new {@link ForLoopNode} with given variable, start and end expression,
     * and optionally step expression.
     *
     * @param variable        Variable of the for loop
     * @param startExpression Start expression of the for loop
     * @param endExpression   End expression of the for loop
     * @param stepExpression  Step expression of the for loop, can be null
     */
    public ForLoopNode(ElementVariable variable, Element startExpression,
                       Element endExpression, Element stepExpression) {
        Objects.requireNonNull(variable, "Variable can not be null!");
        Objects.requireNonNull(startExpression, "Start expression can not be null!");
        Objects.requireNonNull(endExpression, "End expression can not be null!");

        this.variable = variable;
        this.startExpression = startExpression;
        this.endExpression = endExpression;
        this.stepExpression = stepExpression;
    }

    public ElementVariable getVariable() {
        return variable;
    }

    public Element getStartExpression() {
        return startExpression;
    }

    public Element getEndExpression() {
        return endExpression;
    }

    public Element getStepExpression() {
        return stepExpression;
    }

    @Override
    public void addChildNode(Node child) {
        Objects.requireNonNull(child, "Child can not be null!");

        if (children == null) {
            children = new ArrayIndexedCollection();
        }

        children.add(child);
    }

    @Override
    public int numberOfChildren() {
        if (children == null) {
            return 0;
        }

        return children.size();
    }

    @Override
    public Node getChild(int index) {
        return (Node) children.get(index);
    }

    @Override
    public void accept(INodeVisitor visitor) {
        visitor.visitForLoopNode(this);
    }

    @Override
    public String toString() {
        StringBuilder forLoopString = new StringBuilder();

        forLoopString.append("{$ FOR ");
        forLoopString.append(variable.asText()).append(" ");
        forLoopString.append(startExpression.asText()).append(" ");
        forLoopString.append(endExpression.asText()).append(" ");
        if (stepExpression != null) {
            forLoopString.append(stepExpression.asText()).append(" ");
        }
        forLoopString.append("$}");

        for (int i = 0; i < numberOfChildren(); i++) {
            forLoopString.append(children.get(i).toString());
        }

        forLoopString.append("{$END$}");

        return forLoopString.toString();
    }
}
