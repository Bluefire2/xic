package lexer;
import java_cup.runtime.*;

public class XiToken extends Symbol {

    private TokenType type;
    private int line;
    private int col;
    private Object value;

    XiToken(TokenType type, int line, int col, int left, int right, Object value) {
        super(type.ordinal(), left, right, value);
        this.type = type;
        this.line = line + 1; // flex outputs 0-indexed line, we need 1-indexed
        this.col = col + 1;
        this.value = value;
    }

    XiToken(TokenType type, int line, int col, Object value) {
        this(type, line, col, -1, -1, value);
    }

    public TokenType getType() {
        return type;
    }

    public int getLine() {
        return line;
    }

    public int getCol() {
        return col;
    }

    public Object getValue() {
        return value;
    }

    public boolean isError() {
        return getType() == TokenType.ERROR;
    }

    private String format() {
        String s = getValue().toString();
        switch (getType()) {
            case STRING_LIT:
            case CHAR_LIT:
                return s.replace("\\", "\\\\")
                        .replace("\n", "\\n")
                        .replace("\r", "\\r")
                        .replace("\t", "\\t")
                        .replace("\"", "\\\"")
                        .replace("\'", "\\'");
            default: return s;
        }
    }

    public String toString() {
        String type_rep = "";
        switch (getType()) {
            case INT_LIT:       type_rep = "integer "; break;
            case STRING_LIT:    type_rep = "string "; break;
            case CHAR_LIT:      type_rep = "character "; break;
            case ID:            type_rep = "id "; break;
            case ERROR:         type_rep = "error:"; break;
            default:            break;
        }
        return getLine() + ":" + getCol() + " " + type_rep + format();
    }
}