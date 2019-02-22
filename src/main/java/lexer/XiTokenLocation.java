package lexer;

import java_cup.runtime.ComplexSymbolFactory;

public class XiTokenLocation  extends ComplexSymbolFactory.Location {
    private int line;
    private int col;

    public XiTokenLocation(int line, int col) {
        super(line, col);
        this.line = line;
        this.col = col;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getColumn() {
        return col;
    }

    public String toString() {
        return getLine() + ":" + getColumn();
    }
}
