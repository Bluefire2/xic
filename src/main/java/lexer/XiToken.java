package lexer;

enum TokenType {
    // identifiers/variables
    ID,
    UNDERSCORE,

    // literals
    INT_LIT,
    BOOL_LIT,
    STRING_LIT,
    CHAR_LIT,

    // type variables
    INT_TYPE,
    BOOL_TYPE,

    // keywords
    USE,
    IF,
    WHILE,
    ELSE,
    RETURN,
    LENGTH,

    // operators
    EQ, //=
    MINUS,
    PLUS,
    NOT,
    MULT,
    HI_MULT,// *>>
    DIV,
    MOD,
    EQEQ,// ==
    NEQ,
    GT,
    LT,
    GTEQ,
    LTEQ,
    AND,
    OR,

    // separators
    COLON,
    SEMICOLON,
    COMMA,
    LPAREN,
    RPAREN,
    LBRAC,
    RBRAC,
    LCURL,
    RCURL,
    ERROR
}

public class XiToken {

    private TokenType type;
    private int line;
    private int col;
    private Object value;

    XiToken(TokenType type, int line, int col, Object value) {
        this.type = type;
        this.line = line;
        this.col = col;
        this.value = value;
    }

    private String format(String s) {
        s = s.replace("\\", "\\\\");
        s = s.replace("\n", "\\n");
        s = s.replace("\r", "\\r");
        s = s.replace("\t", "\\t");
        s = s.replace("\"", "\\\"");
        s = s.replace("\'", "\\'");
        return s;
    }

    public String toString() {
        String type_rep = "";
        switch (type) {
            case INT_LIT:       type_rep = "integer "; break;
            case STRING_LIT:    type_rep = "string "; break;
            case CHAR_LIT:      type_rep = "character "; break;
            case ID:            type_rep = "id "; break;
            case ERROR:         type_rep = "error:"; break;
            default:            break;
        }
        // make line and col 1-indexed
        return (line+1) + ":" + (col+1) + " " + type_rep + format(value.toString());
    }

    public boolean isError() {
        return type == TokenType.ERROR;
    }
}
