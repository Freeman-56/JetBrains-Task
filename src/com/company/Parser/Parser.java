package com.company.Parser;

import com.company.AstTree.AstNode;
import com.company.AstTree.AstNodeType;

public class Parser {
    private ParserBase parserBase = new ParserBase();

    public void setSource(String source){
        parserBase.setSourceAndDefault(source);
    }

    //Integer
    private AstNode number() throws Exception {
        StringBuilder num = new StringBuilder();
        while(Character.isDigit(parserBase.current())) {
            num.append(parserBase.current());
            parserBase.next();
        }
        if(num.length() == 0)
            throw new Exception("Number expected");
        parserBase.skip();
        return new AstNode(AstNodeType.INTEGER, num.toString());
    }

    //Identifier
    private AstNode ident() throws Exception {
        StringBuilder identifier = new StringBuilder();
        if(Character.isLetter(parserBase.current())) {
            identifier.append(parserBase.current());
            parserBase.next();
            while (Character.isLetterOrDigit(parserBase.current())) {
                identifier.append(parserBase.current());
                parserBase.next();
            }
        }else
            throw new Exception("Identifier expected");
        parserBase.skip();
        return new AstNode(AstNodeType.IDENTIFIER, identifier.toString());
    }

    //SimpleExpression → Identifier | Integer | ( Expression )
    private AstNode simpleExpression() throws Exception {
        if(parserBase.isMatch("(")){
            parserBase.match("(");
            AstNode result = expression();
            parserBase.match(")");
            return result;
        }else if(Character.isLetter(parserBase.current())){
            return ident();
        }else
            return number();
    }

    // BinaryExpression -> ConditionExpression | PlusMinusExpression | MultDivExpression
    private AstNode binaryExpression(int method) throws Exception {
        int beginPos = parserBase.getPos();
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
        while((!parserBase.isMatch(match))
                && parserBase.current() != ';'
                && parserBase.current() != '{'
                && parserBase.current() != 0
                && parserBase.current() != ')'){
            parserBase.next();
        }
        if(parserBase.isMatch(match)){
            parserBase.setBuffer(parserBase.getSource().substring(beginPos, parserBase.getPos()));
            parserBase.setBufferPosBegin();
            String operation = parserBase.match(match);
            AstNode temp2;
            if(newMethod2 != -1)
                temp2 = binaryExpression(newMethod2);
            else
                temp2 = simpleExpression();
            parserBase.setUseBuffer(true);
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
            parserBase.setPos(beginPos);
            if(newMethod2 != -1)
                return binaryExpression(newMethod2);
            else
                return simpleExpression();
        }
    }

    // Expression -> ConditionExpression
    private AstNode expression() throws Exception {
        parserBase.setUseBuffer(false);
        return binaryExpression(1);
    }

    //BlockStatement → { StatementList }
    private AstNode blockStatement() throws Exception {
        parserBase.match("{");
        AstNode result = statementList();
        parserBase.match("}");
        return result;
    }

    //AssignStatement → @ Identifier = Expression
    private AstNode assignStatement() throws Exception {
        parserBase.match("@");
        AstNode id = ident();
        parserBase.match("=");
        AstNode expression = expression();
        parserBase.match(";");
        return new AstNode(AstNodeType.ASSIGN_STATEMENT, id, expression);
    }

    //IfStatement → if ( Expression ) Statement
    private AstNode ifStatement() throws Exception {
        parserBase.match("if");
        parserBase.match("(");
        AstNode expr = expression();
        parserBase.setUseBuffer(false);
        parserBase.match(")");
        AstNode statement = statement();
        return new AstNode(AstNodeType.IF_STATEMENT, expr, statement);
    }

    //ExpressionStatement -> Expression ;
    private AstNode exprStatement() throws Exception {
        AstNode expr = expression();
        parserBase.setUseBuffer(false);
        parserBase.match(";");
        return new AstNode(AstNodeType.EXPRESSION_STATEMENT, expr);
    }

    //Statement → ExpressionStatement
    // | IfStatement | AssignStatement | BlockStatement
    private AstNode statement() throws Exception {
        parserBase.setUseBuffer(false);
        if(parserBase.isMatch("if"))
            return ifStatement();
        else if(parserBase.isMatch("@"))
            return assignStatement();
        else if(parserBase.isMatch("{"))
            return blockStatement();
        else
            return exprStatement();
    }

    //StatementList → empty | StatementList Statement
    private AstNode statementList() throws Exception {
        AstNode stateList = new AstNode(AstNodeType.STATEMENT_LIST);
        while(!parserBase.end()) {
            if(parserBase.isMatch("{", "}"))
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
        parserBase.skip();
        AstNode program = program();
        if(parserBase.end())
            return program;
        else
            throw new Exception("Not the end");
    }

}
