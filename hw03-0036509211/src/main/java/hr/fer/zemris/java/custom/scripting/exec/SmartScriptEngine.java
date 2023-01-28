package hr.fer.zemris.java.custom.scripting.exec;

import hr.fer.zemris.java.custom.scripting.elems.*;
import hr.fer.zemris.java.custom.scripting.nodes.*;
import hr.fer.zemris.java.webserver.RequestContext;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SmartScriptEngine {

    private final DocumentNode documentNode;
    private final RequestContext requestContext;
    private final ObjectMultistack multistack = new ObjectMultistack();
    private final INodeVisitor visitor = new INodeVisitor() {
        @Override
        public void visitTextNode(TextNode node) {
            try {
                requestContext.write(node.getText());
            } catch (IOException e) {
                System.out.println("Can't write to output stream!");
            }
        }

        @Override
        public void visitForLoopNode(ForLoopNode node) {
            String variable = node.getVariable().asText();
            ValueWrapper startExpressionValue = new ValueWrapper(node.getStartExpression().asText());
            multistack.push(variable, startExpressionValue);

            ValueWrapper endExpressionValue = new ValueWrapper(node.getEndExpression().asText());
            ValueWrapper stepExpressionValue = new ValueWrapper(node.getStepExpression().asText());
            while (multistack.peek(variable).numCompare(endExpressionValue.getValue()) <= 0) {
                for (int i = 0; i < node.numberOfChildren(); i++) {
                    node.getChild(i).accept(this);
                }
                multistack.peek(variable).add(stepExpressionValue.getValue());
            }
            multistack.pop(variable);
        }

        @Override
        public void visitEchoNode(EchoNode node) {
            Stack<Object> temporaryStack = new Stack<>();
            for (Element token : node.getElements()) {

                if (token instanceof ElementString || token instanceof ElementConstantDouble || token instanceof ElementConstantInteger) {
                    temporaryStack.push((token.asText()));
                } else if (token instanceof ElementVariable) {
                    ValueWrapper variableValueWrapper = multistack.peek(token.asText());
                    temporaryStack.push(variableValueWrapper.getValue());
                } else if (token instanceof ElementOperator) {
                    Object arg2Object = temporaryStack.pop();
                    Object arg1Object = temporaryStack.pop();

                    ValueWrapper arg1ValueWrapper = new ValueWrapper(arg1Object);
                    ValueWrapper arg2ValueWrapper = new ValueWrapper(arg2Object);
                    switch (token.asText()) {
                        case "+" -> arg1ValueWrapper.add(arg2ValueWrapper.getValue());
                        case "-" -> arg1ValueWrapper.subtract(arg2ValueWrapper.getValue());
                        case "*" -> arg1ValueWrapper.multiply(arg2ValueWrapper.getValue());
                        case "/" -> arg1ValueWrapper.divide(arg2ValueWrapper.getValue());
                        default -> throw new RuntimeException("Unsupported operator: " + token.asText());
                    }
                    temporaryStack.push(arg1ValueWrapper.getValue());
                } else if (token instanceof ElementFunction) {
                    switch (token.asText()) {
                        case "@sin" -> {
                            Object argObject = temporaryStack.pop();
                            ValueWrapper argValueWrapper = new ValueWrapper(argObject);

                            ValueWrapper cheatArgToConvertToDouble = new ValueWrapper(0.0);
                            cheatArgToConvertToDouble.add(argValueWrapper.getValue());
                            temporaryStack.push(Math.sin((double) cheatArgToConvertToDouble.getValue() * Math.PI / 180));
                        }
                        case "@decfmt" -> {
                            DecimalFormat f = new DecimalFormat(temporaryStack.pop().toString());
                            Object argObject = temporaryStack.pop();
                            ValueWrapper argValueWrapper = new ValueWrapper(argObject);
                            temporaryStack.push(new ValueWrapper(f.format(argValueWrapper.getValue())));
                        }
                        case "@dup" -> {
                            Object argObject = temporaryStack.pop();
                            ValueWrapper argValueWrapper = new ValueWrapper(argObject);
                            temporaryStack.push(argValueWrapper.getValue());
                            temporaryStack.push(argValueWrapper.getValue());
                        }
                        case "@swap" -> {
                            Object arg2Object = temporaryStack.pop();
                            Object arg1Object = temporaryStack.pop();

                            temporaryStack.push(arg2Object);
                            temporaryStack.push(arg1Object);
                        }
                        case "@setMimeType" -> requestContext.setMimeType(String.valueOf(temporaryStack.pop()));
                        case "@paramGet" -> {
                            Object defValue = temporaryStack.pop();
                            String name = String.valueOf(temporaryStack.pop());

                            Object value = requestContext.getParameter(name);
                            temporaryStack.push(value == null ? defValue : value);
                        }
                        case "@pparamGet" -> {
                            Object defValue = temporaryStack.pop();
                            String name = String.valueOf(temporaryStack.pop());

                            Object value = requestContext.getPersistentParameter(name);
                            temporaryStack.push(value == null ? defValue : value);
                        }
                        case "@pparamSet" -> {
                            String name = String.valueOf(temporaryStack.pop());
                            String value = String.valueOf(temporaryStack.pop());

                            requestContext.setPersistentParameter(name, value);
                        }
                        case "@pparamDel" -> requestContext.removePersistentParameter(String.valueOf(temporaryStack.pop()));
                        case "@tparamGet" -> {
                            Object defValue = temporaryStack.pop();
                            String name = String.valueOf(temporaryStack.pop());

                            Object value = requestContext.getTemporaryParameter(name);
                            temporaryStack.push(value == null ? defValue : value);
                        }
                        case "@tparamSet" -> {
                            String name = String.valueOf(temporaryStack.pop());
                            String value = String.valueOf(temporaryStack.pop());

                            requestContext.setTemporaryParameter(name, value);
                        }
                        case "@tparamDel" -> requestContext.removeTemporaryParameter(String.valueOf(temporaryStack.pop()));
                        default -> throw new RuntimeException("Unsupported operator: " + token.asText());
                    }
                }
            }
            List<Object> list = new ArrayList<>(temporaryStack);
            StringBuilder writeString = new StringBuilder();

            for (Object element : list) {
                writeString.append(element.toString());
            }
            try {
                requestContext.write(writeString.toString());
            } catch (IOException e) {
                System.out.println("Can't write to output stream!");
            }
        }

        @Override
        public void visitDocumentNode(DocumentNode node) {
            for (int i = 0; i < node.numberOfChildren(); i++) {
                node.getChild(i).accept(this);
            }
        }
    };

    public SmartScriptEngine(DocumentNode documentNode, RequestContext requestContext) {
        this.documentNode = documentNode;
        this.requestContext = requestContext;
    }

    public void execute() {
        documentNode.accept(visitor);
    }

}
