package hr.fer.zemris.java.custom.scripting.nodes;


import hr.fer.zemris.java.custom.collections.ArrayIndexedCollection;

import java.util.Objects;

/**
 * Class which represents document node and extends {@link Node}.
 *
 * @author Filip Vucic
 */
public class DocumentNode extends Node {

    /**
     * {@link DocumentNode} children.
     */
    private ArrayIndexedCollection children;

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
        visitor.visitDocumentNode(this);
    }

    @Override
    public String toString() {
        StringBuilder documentString = new StringBuilder();
        for (int i = 0; i < children.size(); i++) {
            documentString.append(children.get(i).toString());
        }

        return documentString.toString();
    }
}
