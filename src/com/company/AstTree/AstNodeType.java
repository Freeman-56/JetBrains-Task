package com.company.AstTree;

public class AstNodeType {
    public static final int UNKNOWN = 0;

    public static final int INTEGER = 1;
    public static final int IDENTIFIER = 2;

    public static final int ADD = 11;
    public static final int SUB = 12;
    public static final int MUL = 13;
    public static final int DIV = 14;
    public static final int GREATER = 15;
    public static final int LESS = 16;

    public static final int ASSIGN_STATEMENT = 21;
    public static final int IF_STATEMENT = 22;
    public static final int EXPRESSION_STATEMENT = 23;

    public static final int STATEMENT_LIST = 31;

}
