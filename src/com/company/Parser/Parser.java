package com.company.Parser;

import com.company.AstTree.AstNode;
import com.company.AstTree.AstNodeType;

public class Parser extends ParserBase {

    private int oldIfAmount = 0;
    private int newIfAmount = 0;

    public void setSource(String source){
        setSourceAndDefault(source);
        newIfAmount = 0;
    }

    //Integer
    public AstNode number() throws Exception {
        StringBuilder num = new StringBuilder();
        while(Character.isDigit(current())) {
            num.append(current());
            next();
        }
        if(num.length() == 0)
            throw new Exception("Number expected");
        skip();
        return new AstNode(AstNodeType.INTEGER, num.toString());
    }

    //Identifier
    public AstNode ident() throws Exception {
        StringBuilder identifier = new StringBuilder();
        if(Character.isLetter(current())) {
            identifier.append(current());
            next();

            while (Character.isLetterOrDigit(current())) {
                identifier.append(current());
                next();
            }
        }else
            throw new Exception("Identifier expected");
        skip();
        return new AstNode(AstNodeType.IDENTIFIER, identifier.toString());
    }

    //SimpleExpression → Identifier | Integer | ( Expression )
    public AstNode simpleExpression() throws Exception {
        if(isMatch("(")){
            match("(");
            AstNode result = expression();
            match(")");
            return result;
        }else if(Character.isLetter(current())){
            int pos = getPos();
            return ident();
        }else
            return number();
    }

    //MultiplyDivisionExpression → SimpleExpression
    // | MultiplyDivisionExpression * SimpleExpression
    // | MultiplyDivisionExpression / SimpleExpression
    public AstNode multDivExpression() throws Exception {
        int beginPos = getPos();
        while((!isMatch("*", "/")) && current() != ';'
                && current() != '{'
                && current() != 0){
            next();
        }
        if(isMatch("*", "/")){
            buffer = super.getSource().substring(beginPos, getPos());
            String operation = match("*", "/");
            AstNode temp2 = simpleExpression();
            isUseBuffer = true;
            AstNode temp1 = multDivExpression();
            return operation.equals("*") ? new AstNode(AstNodeType.MUL, temp1, temp2)
                    : new AstNode(AstNodeType.DIV, temp1, temp2);
        }else {
            setPos(beginPos);
            return simpleExpression();
        }
    }

    //PlusMinusExpression → MultiplyDivisionExpression
    // | PlusMinusExpression + MultiplyDivisionExpression
    // | PlusMinusExpression - MultiplyDivisionExpression
    public AstNode plusMinusExpression() throws Exception {
        int beginPos = getPos();
        while((!isMatch("+", "-")) && current() != ';'
                && current() != '{'
                && current() != 0){
            next();
        }
        if(isMatch("+", "-")){
            buffer = super.getSource().substring(beginPos, getPos());
            bufferPos = 0;
            String operation = match("+", "-");
            AstNode temp2 = multDivExpression();
            isUseBuffer = true;
            AstNode temp1 = plusMinusExpression();
            return operation.equals("+") ? new AstNode(AstNodeType.ADD, temp1, temp2)
                    : new AstNode(AstNodeType.SUB, temp1, temp2);
        }else {
            setPos(beginPos);
            return multDivExpression();
        }
    }

    //ConditionExpression -> PlusMinusExpression
    // | PlusMinusExpression < PlusMinusExpression
    // | PlusMinusExpression > PlusMinusExpression
    public AstNode conditionExpression() throws Exception {
        int beginPos = getPos();
        while((!isMatch(">", "<")
                && current() != ';' && current() != 0)){
            next();
        }
        if(isMatch(">", "<")){
            buffer = super.getSource().substring(beginPos, getPos());
            bufferPos = 0;
            String operation = match(">", "<");
            AstNode temp2 = plusMinusExpression();
            isUseBuffer = true;
            AstNode temp1 = plusMinusExpression();
            return operation.equals(">") ? new AstNode(AstNodeType.GREATER, temp1, temp2)
                    : new AstNode(AstNodeType.LESS, temp1, temp2);
        }else{
            setPos(beginPos);
            return plusMinusExpression();
        }
    }

    // Expression -> ConditionExpression
    public AstNode expression() throws Exception {
        isUseBuffer = false;
        return conditionExpression();
    }

    //BlockStatement → { StatementList }
    public AstNode blockStatement() throws Exception {
        match("{");
        AstNode result = statementList();
        match("}");
        return result;
    }

    //AssignStatement → @ Identifier = Expression
    public AstNode assignStatement() throws Exception {
        match("@");
        AstNode id = ident();
        match("=");
        AstNode expression = expression();
        match(";");
        return new AstNode(AstNodeType.ASSIGN_STATEMENT, id, expression);
    }

    //IfStatement → if ( Expression ) Statement
    public AstNode ifStatement() throws Exception {
        match("if");
        match("(");
        AstNode expr = expression();
        isUseBuffer = false;
        match(")");
        AstNode statement = statement();
        newIfAmount++;
        return new AstNode(AstNodeType.IF_STATEMENT, expr, statement);
    }

    //ExpressionStatement -> Expression ;
    public AstNode exprStatement() throws Exception {
        AstNode expr = expression();
        isUseBuffer = false;
        match(";");
        return new AstNode(AstNodeType.EXPRESSION_STATEMENT, expr);
    }

    //Statement → ExpressionStatement
    // | IfStatement | AssignStatement | BlockStatement
    private AstNode statement() throws Exception {
        isUseBuffer = false;
        if(isMatch("if"))
            return ifStatement();
        else if(isMatch("@"))
            return assignStatement();
        else if(isMatch("{"))
            return blockStatement();
        else
            return exprStatement();
    }

    //StatementList → empty | StatementList Statement
    public AstNode statementList() throws Exception {
        AstNode stateList = new AstNode(AstNodeType.STATEMENT_LIST);
        while(!end()) {
            if(isMatch("{", "}"))
                break;
            stateList.addChild(statement());
        }
        return stateList;
    }

    //Program → StatementList
    public AstNode program() throws Exception {
        return statementList();
    }

    public AstNode parse() throws Exception {
        skip();
        AstNode program = program();
        if(end())
            return program;
        else
            throw new Exception("Not the end");
    }

    public boolean isIfAdded(){
        if(newIfAmount > oldIfAmount){
            oldIfAmount = newIfAmount;
            return true;
        }
        else return false;
    }

    public void isIfRemoved(){
        if(newIfAmount < oldIfAmount){
            oldIfAmount = newIfAmount;
        }
    }

}
