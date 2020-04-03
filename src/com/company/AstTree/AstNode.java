package com.company.AstTree;

import java.util.ArrayList;
import java.util.List;

public class AstNode {
    public int type;
    public String text;

    private AstNode parent = null;
    private List<AstNode>childs = new ArrayList<>();

    public AstNode(int type, String text, AstNode child1, AstNode child2) {
        this.type = type;
        this.text = text;
        if(child1 != null)
            addChild(child1);
        if(child2 != null)
            addChild(child2);
    }

    public AstNode(int type, AstNode child1, AstNode child2){
        this.type = type;
        if(child1 != null)
            addChild(child1);
        if(child2 != null)
            addChild(child2);
    }

    public AstNode(int type, AstNode child){
        this.type = type;
        if(child != null)
            addChild(child);
    }

    public AstNode(int type, String text){
        this.type = type;
        this.text = text;
    }

    public AstNode(int type){
        this.type = type;
    }

    public void addChild(AstNode child){
        if(child.getParent() != null)
            child.getParent().childs.remove(child);
        childs.remove(child);
        childs.add(child);
        child.parent = this;

    }

    public void removeChild(AstNode child){
        childs.remove(child);
        if(child.parent == this)
            child.parent = null;
    }

    public int childCount(){
        return childs.size();
    }

    public AstNode getParent() {
        return parent;
    }

    public AstNode getChild(int index) {
        return childs.get(index);
    }


}
