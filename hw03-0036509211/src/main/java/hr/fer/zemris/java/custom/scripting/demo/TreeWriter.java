package hr.fer.zemris.java.custom.scripting.demo;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementString;
import hr.fer.zemris.java.custom.scripting.nodes.*;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParserException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TreeWriter {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("File name expected.");
            System.exit(0);
        }

        String docBody = null;
        try {
            docBody = Files.readString(Path.of(args[0]));
        } catch (IOException e) {
            System.out.println("Unable to read file!");
            System.exit(-1);
        }

        SmartScriptParser p = null;
        try {
            p = new SmartScriptParser(docBody);
        } catch (SmartScriptParserException e) {
            System.out.println("Unable to parse document!");
            System.exit(-1);
        } catch (Exception e) {
            System.out.println("If this line ever executes, you have failed this class!");
            System.exit(-1);
        }
        WriterVisitor visitor = new WriterVisitor();
        p.getDocumentNode().accept(visitor);

    }

    private static class WriterVisitor implements INodeVisitor {

        private final StringBuilder writerSB;

        public WriterVisitor() {
            writerSB = new StringBuilder();
        }

        @Override
        public void visitTextNode(TextNode node) {
            writerSB.append(node.getText());
        }

        @Override
        public void visitForLoopNode(ForLoopNode node) {
            writerSB.append("{$ FOR ");
            writerSB.append(node.getVariable().asText()).append(" ");
            writerSB.append(node.getStartExpression().asText()).append(" ");
            writerSB.append(node.getEndExpression().asText()).append(" ");
            if (node.getStepExpression() != null) {
                writerSB.append(node.getStepExpression().asText()).append(" ");
            }
            writerSB.append("$}");

            for (int i = 0; i < node.numberOfChildren(); i++) {
                node.getChild(i).accept(this);
            }

            writerSB.append("{$END$}");
        }

        @Override
        public void visitEchoNode(EchoNode node) {
            writerSB.append("{$= ");

            for (Element element : node.getElements()) {
                if (element instanceof ElementString) {
                    writerSB.append("\"").append(element.asText()).append("\"");
                } else {
                    writerSB.append(element.asText());
                }
                writerSB.append(" ");
            }

            writerSB.append("$}");
        }

        @Override
        public void visitDocumentNode(DocumentNode node) {
            for (int i = 0; i < node.numberOfChildren(); i++) {
                node.getChild(i).accept(this);
            }

            System.out.println(writerSB.toString());
        }
    }
}
