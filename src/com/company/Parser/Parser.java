package com.company.Parser;

import com.company.AstTree.AstNode;
import com.company.AstTree.AstNodeType;

public class Parser extends ParserBase {

    public void setSource(String source){
        setSourceAndDefault(source);
    }

    //Integer
    private AstNode number() throws Exception {
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
    private AstNode ident() throws Exception {
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
    private AstNode simpleExpression() throws Exception {
        if(isMatch("(")){
            match("(");
            AstNode result = expression();
            match(")");
            return result;
        }else if(Character.isLetter(current())){
            return ident();
        }else
            return number();
    }

    // BinaryExpression -> ConditionExpression | PlusMinusExpression | MultDivExpression
    private AstNode binaryExpression(int method) throws Exception {
        int beginPos = getPos();
        int newMethod1;
        int newMethod2;
        String[] match;
        switch (method){
            //ConditionExpression -> PlusMinusExpression
            // | PlusMinusExpression < PlusMinusExpression
            // | PlusMinusExpression > PlusMinusExpression
            case 1:
                match = new String[]{">", "<"};
                newMethod1 = 2;
                newMethod2 = 2;
                break;
            //PlusMinusExpression → MultiplyDivisionExpression
            // | PlusMinusExpression + MultiplyDivisionExpression
            // | PlusMinusExpression - MultiplyDivisionExpression
            case 2:
                match = new String[]{"+", "-"};
                newMethod1 = 2;
                newMethod2 = 3;
                break;
            //MultiplyDivisionExpression → SimpleExpression
            // | MultiplyDivisionExpression * SimpleExpression
            // | MultiplyDivisionExpression / SimpleExpression
            case 3:
                match = new String[]{"*", "/"};
                newMethod1 = 3;
                newMethod2 = -1;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + method);
        }
        while((!isMatch(match))
                && current() != ';'
                && current() != '{'
                && current() != 0
                && current() != ')'){
            next();
        }
        if(isMatch(match)){
            setBuffer(super.getSource().substring(beginPos, getPos()));
            setBufferPosBegin();
            String operation = match(match);
            AstNode temp2;
            if(newMethod2 != -1)
                temp2 = binaryExpression(newMethod2);
            else
                temp2 = simpleExpression();
            setUseBuffer(true);
            AstNode temp1 = binaryExpression(newMethod1);
            switch (method){
                case 1:
                    return operation.equals(">") ? new AstNode(AstNodeType.GREATER, temp1, temp2)
                            : new AstNode(AstNodeType.LESS, temp1, temp2);
                case 2:
                    return operation.equals("+") ? new AstNode(AstNodeType.ADD, temp1, temp2)
                            : new AstNode(AstNodeType.SUB, temp1, temp2);
                case 3:
                    return operation.equals("*") ? new AstNode(AstNodeType.MUL, temp1, temp2)
                            : new AstNode(AstNodeType.DIV, temp1, temp2);
                default: throw new Exception("Wrong method");
            }
        }else {
            setPos(beginPos);
            if(newMethod2 != -1)
                return binaryExpression(newMethod2);
            else
                return simpleExpression();
        }
    }

    // Expression -> ConditionExpression
    private AstNode expression() throws Exception {
        setUseBuffer(false);
        return binaryExpression(1);
    }

    //BlockStatement → { StatementList }
    private AstNode blockStatement() throws Exception {
        match("{");
        AstNode result = statementList();
        match("}");
        return result;
    }

    //AssignStatement → @ Identifier = Expression
    private AstNode assignStatement() throws Exception {
        match("@");
        AstNode id = ident();
        match("=");
        AstNode expression = expression();
        match(";");
        return new AstNode(AstNodeType.ASSIGN_STATEMENT, id, expression);
    }

    //IfStatement → if ( Expression ) Statement
    private AstNode ifStatement() throws Exception {
        match("if");
        match("(");
        AstNode expr = expression();
        setUseBuffer(false);
        match(")");
        AstNode statement = statement();
        return new AstNode(AstNodeType.IF_STATEMENT, expr, statement);
    }

    //ExpressionStatement -> Expression ;
    private AstNode exprStatement() throws Exception {
        AstNode expr = expression();
        setUseBuffer(false);
        match(";");
        return new AstNode(AstNodeType.EXPRESSION_STATEMENT, expr);
    }

    //Statement → ExpressionStatement
    // | IfStatement | AssignStatement | BlockStatement
    private AstNode statement() throws Exception {
        setUseBuffer(false);
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
    private AstNode statementList() throws Exception {
        AstNode stateList = new AstNode(AstNodeType.STATEMENT_LIST);
        while(!end()) {
            if(isMatch("{", "}"))
                break;
            stateList.addChild(statement());
        }
        return stateList;
    }

    //Program → StatementList
    private AstNode program() throws Exception {
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

}
