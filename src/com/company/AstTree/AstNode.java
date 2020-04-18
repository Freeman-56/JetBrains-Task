package com.company.AstTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AstNode {
    private AstNodeType type;
    private String text;

    private AstNode parent = null;
    private List<AstNode>childs = new ArrayList<>();

    public AstNode(AstNodeType type, AstNode child1, AstNode child2){
        this.type = type;
        if(child1 != null)
            addChild(child1);
        if(child2 != null)
            addChild(child2);
    }

    public AstNode(AstNodeType type, AstNode child){
        this.type = type;
        if(child != null)
            addChild(child);
    }

    public AstNode(AstNodeType type, String text){
        this.type = type;
        this.text = text;
    }

    public AstNode(AstNodeType type){
        this.type = type;
    }

    public void addChild(AstNode child){
        if(child.getParent() != null)
            child.getParent().childs.remove(child);
        childs.remove(child);
        childs.add(child);
        child.parent = this;
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

    public List<AstNode> getChilds() {
        return childs;
    }

    public AstNodeType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AstNode astNode = (AstNode) o;
        return type == astNode.type &&
                Objects.equals(text, astNode.text) &&
                Objects.equals(childs, astNode.childs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, text, childs);
    }
}
