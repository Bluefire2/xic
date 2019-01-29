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
    RCURL
}

class XiToken {

    private TokenType type;
    private int line;
    private int col;
    private Object value;

    public XiToken(TokenType type, int line, int col, Object value) {
        this.type = type;
        this.line = line;
        this.col = col;
        this.value = value;
    }

    public String toString() {
        String type_rep = "";
        switch (type) {
            case INT_LIT:       type_rep = "int "; break;
            case BOOL_LIT:      type_rep = "bool "; break;
            case STRING_LIT:    type_rep = "string "; break;
            case CHAR_LIT:      type_rep = "character "; break;
            case ID:            type_rep = "id "; break;
            default:            break;
        }
        return line + ":" + col + " " + type_rep + value.toString();
    }
}
