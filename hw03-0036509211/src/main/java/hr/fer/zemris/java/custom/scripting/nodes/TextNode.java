package hr.fer.zemris.java.custom.scripting.nodes;

/**
 * Class which represents text node and extends {@link Node}.
 *
 * @author Filip Vucic
 */
public class TextNode extends Node {

    /**
     * Text of the node.
     */
    private final String text;

    /**
     * Create new {@link TextNode} with given text.
     *
     * @param text Given text
     */
    public TextNode(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
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
        visitor.visitTextNode(this);
    }

    @Override
    public String toString() {
        return text;
    }
}
