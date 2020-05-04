package com.company.Parser;

public abstract class ParserBase {
    private String source;
    private String buffer;
    private boolean isUseBuffer;
    private int bufferPos;
    private int pos;

    protected void setSourceAndDefault(String source){
        this.source = source;
        buffer = null;
        isUseBuffer = false;
        bufferPos = 0;
        pos = 0;
    }

    protected char current(){
        if(!isUseBuffer)
            return getPos() < source.length() ? source.charAt(getPos()) : (char)0;
        else
            return getPos() < buffer.length() ? buffer.charAt(getPos()) : (char)0;
    }

    protected boolean end(){
        return current() == 0;
    }

    protected void next(){
        if(!end()) {
            if(!isUseBuffer)
                pos++;
            else
                bufferPos++;
        }
    }

    protected void skip(){
        String DEFAULT_WHITESPACES = " \n\r\t";
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

    protected String match(String... terms) throws Exception {
        String result = matchNoExcept(terms);
        if(result == null) {
            throw new Exception("Bad string");
        }
        else
            return result;

    }

    protected String getSource() {
        return source;
    }

    protected int getPos() {
        if(!isUseBuffer)
            return pos;
        else return bufferPos;
    }

    protected void setPos(int pos) {
        if(!isUseBuffer)
            this.pos = pos;
        else bufferPos = pos;
    }

    protected void setBuffer(String buffer) {
        this.buffer = buffer;
    }

    protected void setBufferPosBegin() {
        this.bufferPos = 0;
    }

    protected void setUseBuffer(boolean useBuffer) {
        isUseBuffer = useBuffer;
    }
}
