package hr.fer.zemris.java.custom.scripting.nodes;


import hr.fer.zemris.java.custom.scripting.elems.Element;

import java.util.Objects;

/**
 * Class which represents echo node and extends {@link Node}.
 *
 * @author Filip Vucic
 */
public class EchoNode extends Node {

    /**
     * Elements of {@link EchoNode}.
     */
    private final Element[] elements;

    /**
     * Create new {@link EchoNode} with given elements.
     *
     * @param elements Elements
     */
    public EchoNode(Element[] elements) {
        Objects.requireNonNull(elements, "Elements can not be null!");

        this.elements = elements;
    }

    public Element[] getElements() {
        return elements;
    }

    @Override
    public void addChildNode(Node child) {
        throw new UnsupportedOperationException("Echo node can not have children!");
    }

    @Override
    public int numberOfChildren() {
        return 0;
    }

    @Override
    public Node getChild(int index) {
        throw new UnsupportedOperationException("Echo node has no children!");
    }

    @Override
    public void accept(INodeVisitor visitor) {
        visitor.visitEchoNode(this);
    }

    @Override
    public String toString() {
        StringBuilder echoString = new StringBuilder();

        echoString.append("{$ = ");

        for (Element element : elements) {
            echoString.append(element.asText());
            echoString.append(" ");
        }

        echoString.append("$}");

        return echoString.toString();
    }
}
