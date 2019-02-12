package lexer;


public class XiTokenLocation {
    private int line;
    private int col;

    public XiTokenLocation(int line, int col) {
        this.line = line;
        this.col = col;
    }

    public int getLine() {
        return line;
    }

    public int getCol() {
        return col;
    }

    public String toString() {
        return getLine() + ":" + getCol();
    }
}
