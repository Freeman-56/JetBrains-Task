package com.company.Parser;

public class ParserBase {
    public final String DEFAULT_WHITESPACES = " \n\r\t";

    private String source = null;
    protected String buffer = null;
    protected boolean isUseBuffer = false;
    protected int bufferPos = 0;
    private int pos = 0;

    protected void setSourceAndDefault(String source){
        this.source = source;
        buffer = null;
        isUseBuffer = false;
        bufferPos = 0;
        pos = 0;
    }

    public char current(){
        if(!isUseBuffer)
            return getPos() < source.length() ? source.charAt(getPos()) : (char)0;
        else
            return getPos() < buffer.length() ? buffer.charAt(getPos()) : (char)0;
    }

    public boolean end(){
        return current() == 0;
    }

    public void next(){
        if(!end()) {
            if(!isUseBuffer)
                this.pos = this.pos + 1;
            else
                bufferPos++;
        }
    }

    public void skip(){
        while (DEFAULT_WHITESPACES.indexOf(current()) >= 0)
            next();
    }

    protected String matchNoExcept(String ... terms){
        int pos = getPos();
        for(String s : terms){
            boolean match = true;
            for(char c : s.toCharArray()) {
                if (current() == c) {
                    next();
                } else {
                    if (!isUseBuffer)
                        this.pos = pos;
                    else
                        bufferPos = pos;
                    match = false;
                    break;
                }
            }
            if(match){
                skip();
                return s;
            }
        }
        return null;
    }

    protected boolean isMatch(String ... terms){
        int pos = getPos();
        String result = matchNoExcept(terms);
        if(!isUseBuffer)
            this.pos = pos;
        else bufferPos = pos;
        return result != null;
    }

    public String match(String ... terms) throws Exception {
        String result = matchNoExcept(terms);
        if(result == null) {
            throw new Exception("Bad string");
        }
        else
            return result;

    }

    public String getSource() {
        return source;
    }

    public int getPos() {
        if(!isUseBuffer)
            return pos;
        else return bufferPos;
    }

    public void setPos(int pos) {
        if(!isUseBuffer)
            this.pos = pos;
        else bufferPos = pos;
    }
}
