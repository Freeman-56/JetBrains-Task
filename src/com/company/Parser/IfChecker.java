package com.company.Parser;

import com.company.AstTree.AstNode;
import com.company.AstTree.AstNodeType;

import java.util.ArrayList;

public class IfChecker {
    private ArrayList<AstNode> oldNodes = new ArrayList<>();
    private ArrayList<AstNode> newNodes = new ArrayList<>();

    public boolean isRightIfAdded(AstNode program){
        refreshTrees();
        takeNodes(program);
        if(oldNodes.size() == 0){
            oldNodes.addAll(newNodes);
            return false;
        }
        int beginPosOld;
        for (int i = 0; i < getMinSize(); i++) {
            if((oldNodes.get(i).getType() != AstNodeType.IF_STATEMENT
                    && newNodes.get(i).getType() == AstNodeType.IF_STATEMENT)){
                beginPosOld = i; // позиция выражения
                int newNodesPos;

                if(newNodes.get(i + 1).getType() == AstNodeType.INTEGER
                        || newNodes.get(i + 1).getType() == AstNodeType.IDENTIFIER)
                    newNodesPos = i + 2;
                else
                    newNodesPos = i + 4;

                if(newNodes.get(newNodesPos).getType() == AstNodeType.STATEMENT_LIST
                        && newNodes.get(newNodesPos).getChilds().size() > 0)
                    return !isItNewIf(newNodesPos + 1, beginPosOld);// + 1 чтобы перейти к телу
            }else if(oldNodes.get(i).getType() == AstNodeType.IF_STATEMENT
                        && newNodes.get(i).getType() == AstNodeType.IF_STATEMENT){
                    if(!oldNodes.get(i).equals(newNodes.get(i))
                            && newNodes.get(i).getChilds().contains(oldNodes.get(i)))
                        return true;
                }
        }
         return false;
    }

    private boolean isItNewIf(int newNodesPos, int beginPosOld){
        int posOld = beginPosOld;
        int nodeCount = 0;
        boolean repeat = false;
        while(true) {
            while (newNodes.get(newNodesPos).equals(oldNodes.get(posOld))) {
                newNodesPos++;
                posOld++;
                if(!repeat)
                    nodeCount++;
                else nodeCount--;
                if((posOld) == oldNodes.size())
                    break;
            }
            if (!repeat && newNodesPos < newNodes.size()) {
                posOld = beginPosOld;
                repeat = true;
            }else return nodeCount == 0;

        }
    }

    private int getMinSize(){
        return Math.min(newNodes.size(), oldNodes.size());
    }

    private void takeNodes(AstNode child){
        newNodes.add(child);
        for(AstNode node : child.getChilds())
            takeNodes(node);
    }

    private void refreshTrees(){
        oldNodes.clear();
        oldNodes.addAll(newNodes);
        newNodes.clear();
    }
}
