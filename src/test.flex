/**
 * Xi Lexer
 */
%%

/* the name of your lexer class */
%class XiLexer

%type XiToken

/* declare variables */
%{
    // A buffer to start and store strings when lexing
    StringBuffer stringLiteral = new StringBuffer();

    enum TokenType {//types of tokens
        ID, //identifiers/variables
        UNDERSCORE,
        //literals
        INT_LIT,
        BOOL_LIT,
        STRING_LIT,
        CHAR_LIT,
        //type variables
        INT_TYPE,
        BOOL_TYPE,
        //keywords
        USE,
        IF,
        WHILE,
        ELSE,
        RETURN,
        LENGTH,
        //operators
        EQ, //=
        MINUS,
        PLUS,
        NOT,
        MULT,
        HI_MULT,//*>>
        DIV,
        MOD,
        EQEQ, //==
        NEQ,
        GT,
        LT,
        GTEQ,
        LTEQ,
        AND,
        OR,
        //separators
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

        public XiToken(TokenType type, int line, int col, Object value){
            this.type = type;
            this.line = line;
            this.col = col;
            this.value = value;
        }

        public String toString(){
            String type_rep = "";
            switch (type) {
                case INT_LIT:
                    type_rep = "int ";
                    break;
                case BOOL_LIT:
                    type_rep = "bool ";
                    break;
                case STRING_LIT:
                    type_rep = "string ";
                    break;
                case CHAR_LIT:
                    type_rep = "character ";
                    break;
                case ID:
                    type_rep = "id ";
                    break;
                default:
                    break;
            }
            return line+":"+column+" "+type_rep+value.toString();
        }
    }

%}

/* switch line counting on */
%line
%column

/* macro */
EOL = \n
NON_EOL = [^\n]
WHITESPACE = {EOL} | [ \t\f]
HEX = \x([0-9A-Fa-f]{1,4})
ID = [A-Za-z][A-Za-z0-9\'_]* //variables
COMMENT = "//"{NON_EOL}*{EOL}? //{EOL}? because comment may be last line in file
INTEGER = 0 | [1-9][0-9]*

%state STRING, CHAR

%%

/* lexical rules */
<YYINITIAL> {
    "_"  { return new XiToken(TokenType.UNDERSCORE, yyline, yycolumn, yytext());}

    /* keywords */
    "use"  { return new XiToken(TokenType.USE, yyline, yycolumn, yytext());}
    "if"  { return new XiToken(TokenType.IF, yyline, yycolumn, yytext());}
    "while"  { return new XiToken(TokenType.WHILE, yyline, yycolumn, yytext());}
    "else"  { return new XiToken(TokenType.ELSE, yyline, yycolumn, yytext());}
    "return"  { return new XiToken(TokenType.RETURN, yyline, yycolumn, yytext());}
    "length"  { return new XiToken(TokenType.LENGTH, yyline, yycolumn, yytext());}
    "int"  { return new XiToken(TokenType.INT_TYPE, yyline, yycolumn, yytext());}
    "bool"  { return new XiToken(TokenType.BOOL_TYPE, yyline, yycolumn, yytext());}
    "true"  { return new XiToken(TokenType.BOOL_LIT, yyline, yycolumn, true);}
    "false"  { return new XiToken(TokenType.BOOL_LIT, yyline, yycolumn, false);}

    /* identifiers */
    {ID} { return new XiToken(TokenType.ID, yyline, yycolumn, yytext());}

    /* literals */
    {INTEGER} { return new XiToken(TokenType.INT_LIT, yyline, yycolumn,
      new Long(yytext()));}
    \" { stringLiteral.setLength(0); yybegin(STRING); }
    \' { yybegin(CHAR); }

    /* operators */
    "="  { return new XiToken(TokenType.EQ, yyline, yycolumn, yytext());}
    "-"  { return new XiToken(TokenType.MINUS, yyline, yycolumn, yytext());}
    "+"  { return new XiToken(TokenType.PLUS, yyline, yycolumn, yytext());}
    "!"  { return new XiToken(TokenType.NOT, yyline, yycolumn, yytext());}
    "*"  { return new XiToken(TokenType.MULT, yyline, yycolumn, yytext());}
    "*>>"  { return new XiToken(TokenType.HI_MULT, yyline, yycolumn, yytext());}
    "/"  { return new XiToken(TokenType.DIV, yyline, yycolumn, yytext());}
    "%"  { return new XiToken(TokenType.MOD, yyline, yycolumn, yytext());}
    "=="  { return new XiToken(TokenType.EQEQ, yyline, yycolumn, yytext());}
    "!="  { return new XiToken(TokenType.NEQ, yyline, yycolumn, yytext());}
    ">"  { return new XiToken(TokenType.GT, yyline, yycolumn, yytext());}
    "<"  { return new XiToken(TokenType.LT, yyline, yycolumn, yytext());}
    ">="  { return new XiToken(TokenType.GTEQ, yyline, yycolumn, yytext());}
    "<="  { return new XiToken(TokenType.LTEQ, yyline, yycolumn, yytext());}
    "&"  { return new XiToken(TokenType.AND, yyline, yycolumn, yytext());}
    "|"  { return new XiToken(TokenType.OR, yyline, yycolumn, yytext());}

    /* separators */
    ":"  { return new XiToken(TokenType.COLON, yyline, yycolumn, yytext());}
    ";"  { return new XiToken(TokenType.SEMICOLON, yyline, yycolumn, yytext());}
    ","  { return new XiToken(TokenType.COMMA, yyline, yycolumn, yytext());}
    "("  { return new XiToken(TokenType.LPAREN, yyline, yycolumn, yytext());}
    ")"  { return new XiToken(TokenType.RPAREN, yyline, yycolumn, yytext());}
    "["  { return new XiToken(TokenType.LBRAC, yyline, yycolumn, yytext());}
    "]"  { return new XiToken(TokenType.RBRAC, yyline, yycolumn, yytext());}
    "{"  { return new XiToken(TokenType.LCURL, yyline, yycolumn, yytext());}
    "}"  { return new XiToken(TokenType.RCURL, yyline, yycolumn, yytext());}

    /* other */
    {WHITESPACE} {}//ignore
    {COMMENT} {}//ignore
    "-9223372036854775808"  { return new XiToken(TokenType.INT_LIT, yyline, yycolumn, Long.MIN_VALUE); }

}

<STRING> {
    \"              { yybegin(INITIAL);
                      return new XiToken(TokenType.STRING_LIT, yyline, yycolumn,
                        stringLiteral.toString()); }
    [^\n\"\\]+ { stringLiteral.append( yytext() ); }//TODO exclude HEX?
    {HEX}           { stringLiteral.append( Character.valueOf(Integer.parseInt(
                        yytext().substring(2, yylength()-1), 16
                    ))); }
    \\t             { stringLiteral.append( '\t' ); }
    \\n             { stringLiteral.append( '\n' ); }
    \\\"            { stringLiteral.append( '\"' ); }
    \\              { stringLiteral.append( '\\' ); }
}

<CHAR> {
     [^\n\'\\]\'    { yybegin(INITIAL); //TODO exclude HEX?
                    return new XiToken(TokenType.CHAR_LIT, yyline, yycolumn,
                        yytext().charAt(0)); }
     {HEX}\' {yybegin(INITIAL); return new XiToken(TokenType.CHAR_LIT, yyline,
     yycolumn,
       Character.valueOf(Integer.parseInt(yytext().substring(2,yylength()-1),16)));}
     \\t\'          { yybegin(INITIAL); return new XiToken(TokenType
     .CHAR_LIT, yyline, yycolumn, '\t'); }
     \\n\'          { yybegin(INITIAL); return new XiToken(TokenType
     .CHAR_LIT, yyline, yycolumn, '\n'); }
     \\\'           { yybegin(INITIAL); return new XiToken(TokenType
     .CHAR_LIT, yyline, yycolumn, '\\'); }
     \\\'\'         { yybegin(INITIAL); return new XiToken(TokenType
     .CHAR_LIT, yyline, yycolumn, '\''); }
}

/* error fallback */
[^] { throw new Error("Illegal character <"+yytext()+">");}
