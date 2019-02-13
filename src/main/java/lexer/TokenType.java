package lexer;

public enum TokenType {
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
    EQ,//=
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
    ERROR,

    EOF
}

