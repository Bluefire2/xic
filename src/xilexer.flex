package lexer;

%%

/* the name of your lexer class */
%public
%class XiLexer

%type XiToken

/* declare variables */
%{
    // A buffer to start and store strings when lexing
    StringBuffer stringLiteral = new StringBuffer();
    // Store the col where the string or char literal starts
    int stringLiteralStartCol = 0;
    int charLiteralStartCol = 0;
%}

/* switch line counting on */
%line
%column

/* macros */
EOL = \r|\n|\r\n
NON_EOL = [^\r\n]
WHITESPACE = {EOL} | [ \t\f]
HEX = \\x([0-9A-Fa-f]{1,4})
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
    \" { stringLiteral.setLength(0);
      stringLiteralStartCol = yycolumn;
      yybegin(STRING); }
    \' { charLiteralStartCol = yycolumn; yybegin(CHAR); }

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
    \"              { yybegin(YYINITIAL);
                      return new XiToken(TokenType.STRING_LIT, yyline,
                      stringLiteralStartCol, stringLiteral.toString()); }
    [^\n\r\"\\]+ { stringLiteral.append( yytext() ); }
    {HEX}           { stringLiteral.append( (char) Integer.parseInt(
                        yytext().substring(2, yylength()), 16
                    )); }
    \\t             { stringLiteral.append( '\t' ); }
    \\n             { stringLiteral.append( '\n' ); }
    \\r             { stringLiteral.append( '\r' ); }
    \\\"            { stringLiteral.append( '\"' ); }
    \\              { stringLiteral.append( '\\' ); }
}

<CHAR> {
    \'        { yybegin(YYINITIAL);
                return new XiToken(TokenType.ERROR, yyline,
                charLiteralStartCol, "empty character literal"); }
     [^\n\r\'\\]\'    { yybegin(YYINITIAL);
                    return new XiToken(TokenType.CHAR_LIT, yyline,
                    charLiteralStartCol, yytext().charAt(0)); }
     {HEX}\' {yybegin(YYINITIAL); return new XiToken(TokenType.CHAR_LIT, yyline,
     charLiteralStartCol,
       Character.forDigit(
               Integer.parseInt(yytext().substring(2,yylength()) ,16), 10
               ));}
     \\t\'          { yybegin(YYINITIAL); return new XiToken(TokenType
     .CHAR_LIT, yyline, charLiteralStartCol, '\t'); }
     \\n\'          { yybegin(YYINITIAL); return new XiToken(TokenType
     .CHAR_LIT, yyline, charLiteralStartCol, '\n'); }
     \\r\'          { yybegin(YYINITIAL); return new XiToken(TokenType
     .CHAR_LIT, yyline, charLiteralStartCol, '\r'); }
     \\\'           { yybegin(YYINITIAL); return new XiToken(TokenType
     .CHAR_LIT, yyline, charLiteralStartCol, '\\'); }
     \\\'\'         { yybegin(YYINITIAL); return new XiToken(TokenType
     .CHAR_LIT, yyline, charLiteralStartCol, '\''); }
}

/* error fallback */
[^] { return new XiToken(TokenType.ERROR, yyline, yycolumn,
        "illegal symbol <"+yytext()+">");}
