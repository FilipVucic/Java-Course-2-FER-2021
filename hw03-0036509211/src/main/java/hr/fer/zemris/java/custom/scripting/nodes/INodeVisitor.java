package hr.fer.zemris.java.custom.scripting.nodes;

/**
 * Implementation of {@link Node} visitor pattern.
 *
 * @author Filip Vucic
 */
public interface INodeVisitor {

    /**
     * Visit {@link TextNode}.
     *
     * @param node {@link TextNode}
     */
    void visitTextNode(TextNode node);

    /**
     * Visit {@link ForLoopNode}.
     *
     * @param node {@link ForLoopNode}
     */
    void visitForLoopNode(ForLoopNode node);

    /**
     * Visit {@link EchoNode}.
     *
     * @param node {@link EchoNode}
     */
    void visitEchoNode(EchoNode node);

    /**
     * Visit {@link DocumentNode}.
     *
     * @param node {@link DocumentNode}
     */
    void visitDocumentNode(DocumentNode node);
}
