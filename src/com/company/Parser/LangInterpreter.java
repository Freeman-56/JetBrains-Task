package com.company.Parser;

import com.company.AstTree.AstNode;
import com.company.AstTree.AstNodeType;

import java.util.HashMap;

public class LangInterpreter {
    private HashMap<String, Integer> varTable =
            new HashMap<>();

    private AstNode programNode;
    private StringBuilder output = new StringBuilder();

    public LangInterpreter(AstNode programNode) throws Exception {
        if(programNode.getType() != AstNodeType.STATEMENT_LIST)
            throw new Exception("Bad program");
        this.programNode = programNode;
    }

    private int executeNode(AstNode node) throws Exception {
        switch (node.getType()){
            case UNKNOWN:
                System.out.println("UNKNOWN");
            case INTEGER:
                return Integer.parseInt(node.getText());
            case IDENTIFIER:
                if(varTable.containsKey(node.getText()))
                    return varTable.get(node.getText());
                else
                    throw new Exception("Variable not define");
            case ADD:
                return executeNode(node.getChild(0)) + executeNode(node.getChild(1));
            case SUB:
                return executeNode(node.getChild(0)) - executeNode(node.getChild(1));
            case MUL:
                return executeNode(node.getChild(0)) * executeNode(node.getChild(1));
            case DIV:
                return executeNode(node.getChild(0)) / executeNode(node.getChild(1));
            case GREATER:
                if(executeNode(node.getChild(0)) > executeNode(node.getChild(1)))
                    return 1;
                else return 0;
            case LESS:
                if(executeNode(node.getChild(0)) < executeNode(node.getChild(1)))
                    return 1;
                else return 0;
            case ASSIGN_STATEMENT:
                int node1 = executeNode(node.getChild(1));
                if(varTable.containsKey(node.getChild(0).getText())){
                    varTable.replace(node.getChild(0).getText(), node1);
                }else
                    varTable.put(node.getChild(0).getText(), node1);
                break;
            case IF_STATEMENT:
                if(executeNode(node.getChild(0)) != 0)
                    return executeNode(node.getChild(1));
                else break;
            case EXPRESSION_STATEMENT:
                output.append(executeNode(node.getChild(0))).append(" ");
                break;
            case STATEMENT_LIST:
                for(int i = 0;i < node.childCount();i++)
                    executeNode(node.getChild(i));
                break;
            default: throw new Exception("Unknown AST node");
        }
        return 0;
    }

    public void execute() throws Exception {
        executeNode(programNode);
    }

    public String getOutput() {
        return "Output: " + output.toString();
    }
}
