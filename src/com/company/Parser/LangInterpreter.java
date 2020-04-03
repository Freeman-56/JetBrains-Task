package com.company.Parser;

import com.company.AstTree.AstNode;
import com.company.AstTree.AstNodeType;

import java.util.HashMap;

public class LangInterpreter {
    private HashMap<String, Integer> varTable =
            new HashMap<>();

    private AstNode programNode = null;
    private StringBuilder output = new StringBuilder();

    public LangInterpreter(AstNode programNode) throws Exception {
        if(programNode.type != AstNodeType.STATEMENT_LIST)
            throw new Exception("Bad program");
        this.programNode = programNode;
    }

    private int executeNode(AstNode node) throws Exception {
        switch (node.type){
            case AstNodeType.UNKNOWN:
                System.out.println("UNKNOWN");
            case AstNodeType.INTEGER:
                return Integer.parseInt(node.text);
            case AstNodeType.IDENTIFIER:
                if(varTable.containsKey(node.text))
                    return varTable.get(node.text);
                else
                    throw new Exception("Variable not define");
            case AstNodeType.ADD:
                return executeNode(node.getChild(0)) + executeNode(node.getChild(1));
            case AstNodeType.SUB:
                return executeNode(node.getChild(0)) - executeNode(node.getChild(1));
            case AstNodeType.MUL:
                return executeNode(node.getChild(0)) * executeNode(node.getChild(1));
            case AstNodeType.DIV:
                return executeNode(node.getChild(0)) / executeNode(node.getChild(1));
            case AstNodeType.GREATER:
                if(executeNode(node.getChild(0)) > executeNode(node.getChild(1)))
                    return 1;
                else return 0;
            case AstNodeType.LESS:
                if(executeNode(node.getChild(0)) < executeNode(node.getChild(1)))
                    return 1;
                else return 0;
            case AstNodeType.ASSIGN_STATEMENT:
                int node1 = executeNode(node.getChild(1));
                if(varTable.containsKey(node.getChild(0).text)){
                    varTable.replace(node.getChild(0).text, node1);
                }else
                    varTable.put(node.getChild(0).text, node1);
                break;
            case AstNodeType.IF_STATEMENT:
                if(executeNode(node.getChild(0)) != 0)
                    return executeNode(node.getChild(1));
                else break;
            case AstNodeType.EXPRESSION_STATEMENT:
                output.append(executeNode(node.getChild(0))).append(" ");
                break;
            case AstNodeType.STATEMENT_LIST:
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
