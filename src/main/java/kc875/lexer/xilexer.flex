package kc875.lexer;

import java_cup.runtime.*;
import kc875.xi_parser.sym;

%%

/* the name of your lexer class */
%public
%class XiLexer
%unicode
%cup
//%type XiToken

/* switch line and column counting on */
%line
%column

/* declare variables */
%{
    // A buffer to start and store strings when lexing
    private StringBuffer stringLiteral = new StringBuffer();
    private XiTokenFactory symFactory;

    // Store the col where the string or char literal starts
    private int stringLiteralStartCol = 0;
    private int charLiteralStartCol = 0;

    private String errorString = "error:";

    public XiLexer(java.io.Reader in, XiTokenFactory tf){
    	this(in);
    	symFactory = tf;
    }

    // flex generates 0-indexed line, col, we need 1-indexed
    private XiToken symbol(String name, int id, int line, int col, Object val) {
        XiTokenLocation loc = new XiTokenLocation(line+1, col+1);
        return symFactory.newSymbol(name, id, loc, val);
    }

    private XiToken symbol(String name, int id, int line, int col) {
        return symbol(name, id, line, col, name);
    }

%}

/* macros */
EOL = \r|\n|\r\n
NON_EOL = [^\r\n]
WHITESPACE = {EOL} | [ \t\f]
HEX = ([0-9A-Fa-f]{1,4})
// Variables can be both, classes must be capitalized
ID_LOWER = [a-z][A-Za-z0-9\'_]*
ID_UPPER = [A-Z][A-Za-z0-9\'_]*
COMMENT = "//"{NON_EOL}*{EOL}? // {EOL}? since comment may be last line in file
INTEGER = 0 | [1-9][0-9]*

%state STRING, CHAR

%eofval{
    return symbol("EOF", sym.EOF, yyline, yycolumn);
%eofval}

%%

/* lexical rules */
<YYINITIAL> {
    "_"         { return symbol(yytext(), sym.UNDERSCORE, yyline, yycolumn); }

    /* keywords */
    "use"       { return symbol(yytext(), sym.USE, yyline, yycolumn); }
    "if"        { return symbol(yytext(), sym.IF, yyline, yycolumn); }
    "while"     { return symbol(yytext(), sym.WHILE, yyline, yycolumn); }
    "else"      { return symbol(yytext(), sym.ELSE, yyline, yycolumn); }
    "return"    { return symbol(yytext(), sym.RETURN, yyline, yycolumn); }
    "length"    { return symbol(yytext(), sym.LENGTH, yyline, yycolumn); }
    "int"       { return symbol(yytext(), sym.INT_TYPE, yyline, yycolumn); }
    "bool"      { return symbol(yytext(), sym.BOOL_TYPE, yyline, yycolumn); }
    "true"      { return symbol(yytext(), sym.BOOL_LIT, yyline, yycolumn,
        true); }
    "false"     { return symbol(yytext(), sym.BOOL_LIT, yyline, yycolumn,
        false); }
    "null"      { return symbol(yytext(), sym.NULL, yyline, yycolumn); }
    "new"       { return symbol(yytext(), sym.NEW, yyline, yycolumn); }
    "class"     { return symbol(yytext(), sym.CLASS, yyline, yycolumn); }
    "extends"   { return symbol(yytext(), sym.EXTENDS, yyline, yycolumn); }
    "this"      { return symbol(yytext(), sym.THIS, yyline, yycolumn); }
    "break"     { return symbol(yytext(), sym.BREAK, yyline, yycolumn); }

    /* identifiers */
    {ID_LOWER}        { return symbol("id " + yytext(), sym.ID_LOWER, yyline, yycolumn, yytext()); }
    {ID_UPPER}        { return symbol("id " + yytext(), sym.ID_UPPER, yyline, yycolumn, yytext()); }

    /* literals */
    {INTEGER}   {
        try {
            return symbol("integer " + yytext(), sym.INT_LIT,
                yyline, yycolumn, Long.parseLong(yytext()));
        } catch (NumberFormatException e) {
            String message = "invalid integer";
            return symbol(errorString + message, sym.ERROR,
                yyline, yycolumn, message);
        }
    }
    \"          { stringLiteral.setLength(0);
                stringLiteralStartCol = yycolumn;
                yybegin(STRING); }
    \'          { charLiteralStartCol = yycolumn; yybegin(CHAR); }

    /* operators */
    "="     { return symbol(yytext(), sym.EQ, yyline, yycolumn); }
    "-"     { return symbol(yytext(), sym.MINUS, yyline, yycolumn); }
    "+"     { return symbol(yytext(), sym.PLUS, yyline, yycolumn); }
    "!"     { return symbol(yytext(), sym.NOT, yyline, yycolumn); }
    "*"     { return symbol(yytext(), sym.MULT, yyline, yycolumn); }
    "*>>"   { return symbol(yytext(), sym.HI_MULT, yyline, yycolumn); }
    "/"     { return symbol(yytext(), sym.DIV, yyline, yycolumn); }
    "%"     { return symbol(yytext(), sym.MOD, yyline, yycolumn); }
    "=="    { return symbol(yytext(), sym.EQEQ, yyline, yycolumn); }
    "!="    { return symbol(yytext(), sym.NEQ, yyline, yycolumn); }
    ">"     { return symbol(yytext(), sym.GT, yyline, yycolumn); }
    "<"     { return symbol(yytext(), sym.LT, yyline, yycolumn); }
    ">="    { return symbol(yytext(), sym.GTEQ, yyline, yycolumn); }
    "<="    { return symbol(yytext(), sym.LTEQ, yyline, yycolumn); }
    "&"     { return symbol(yytext(), sym.AND, yyline, yycolumn); }
    "|"     { return symbol(yytext(), sym.OR, yyline, yycolumn); }
    "."     { return symbol(yytext(), sym.DOT, yyline, yycolumn); }

    /* separators */
    ":"  { return symbol(yytext(), sym.COLON, yyline, yycolumn); }
    ";"  { return symbol(yytext(), sym.SEMICOLON, yyline, yycolumn); }
    ","  { return symbol(yytext(), sym.COMMA, yyline, yycolumn); }
    "("  { return symbol(yytext(), sym.LPAREN, yyline, yycolumn); }
    ")"  { return symbol(yytext(), sym.RPAREN, yyline, yycolumn); }
    "["  { return symbol(yytext(), sym.LBRAC, yyline, yycolumn); }
    "]"  { return symbol(yytext(), sym.RBRAC, yyline, yycolumn); }
    "{"  { return symbol(yytext(), sym.LCURL, yyline, yycolumn); }
    "}"  { return symbol(yytext(), sym.RCURL, yyline, yycolumn); }

    /* other */
    {WHITESPACE} {} // Ignore
    {COMMENT} {} // Ignore
    "-9223372036854775808"  { return symbol("integer " + yytext(), sym.INT_LIT,
        yyline, yycolumn, Long.MIN_VALUE); }

}

<STRING> {
    \"              { yybegin(YYINITIAL);
                      String s = stringLiteral.toString();
                      return symbol("string " + s, sym.STRING_LIT, yyline,
                        stringLiteralStartCol, s); }
    {EOL}           { yybegin(YYINITIAL);
                      String message = "missing ending double quotes";
                      return symbol(
              errorString + message, sym.ERROR, yyline, stringLiteralStartCol,
              message); }
    [^\n\r\"\\]+    { stringLiteral.append( yytext() ); }
    \\x{HEX}        { stringLiteral.append( (char) Integer.parseInt(
                        yytext().substring(2, yylength()), 16
                    )); }
    \\t             { stringLiteral.append( "\t" ); }
    \\n             { stringLiteral.append( "\n" ); }
    \\r             { stringLiteral.append( "\r" ); }
    \\\"            { stringLiteral.append( "\"" ); }
    \\\'            { stringLiteral.append( "\'" ); }
    \\\\            { stringLiteral.append( "\\" ); }
    // yycolumn instead of string start col because the escape is the
    // problem, not the string itself
    \\.             { yybegin(YYINITIAL);
                      String message = "invalid escape character";
                      return symbol(
              errorString + message, sym.ERROR, yyline, yycolumn, message); }
}

<CHAR> {
    \'              { yybegin(YYINITIAL);
                      String message = "empty character literal";
                      return symbol(
              errorString + message, sym.ERROR, yyline, charLiteralStartCol,
              message); }
    [^\n\r\'\\\"]\' { yybegin(YYINITIAL);
                      Character c = yytext().charAt(0);
                      return symbol("character " + c, sym.CHAR_LIT,
                        yyline, charLiteralStartCol, c); }
    \\x{HEX}\'      { yybegin(YYINITIAL);
                      char c = Character.forDigit(
                              Integer.parseInt(
                                      yytext().substring(2, yylength()), 16
                              ), 10
                      );
                      return symbol("character " + c, sym.CHAR_LIT,
                        yyline, charLiteralStartCol, c); }
    \\t\'           { yybegin(YYINITIAL);
                      char c = '\t';
                      return symbol("character " + c, sym.CHAR_LIT,
                        yyline, charLiteralStartCol, c); }
    \\n\'           { yybegin(YYINITIAL);
                      char c = '\n';
                      return symbol("character " + c, sym.CHAR_LIT,
                        yyline, charLiteralStartCol, c); }
    \\r\'           { yybegin(YYINITIAL);
                      char c = '\r';
                      return symbol("character " + c, sym.CHAR_LIT,
                        yyline, charLiteralStartCol, c); }
    \\\'           { yybegin(YYINITIAL);
                      char c = '\\';
                      return symbol("character " + c, sym.CHAR_LIT,
                        yyline, charLiteralStartCol, c); }
    \\\'\'           { yybegin(YYINITIAL);
                      char c = '\'';
                      return symbol("character " + c, sym.CHAR_LIT,
                        yyline, charLiteralStartCol, c); }
    \"\'           { yybegin(YYINITIAL);
                      char c = '\"';
                      return symbol("character " + c, sym.CHAR_LIT,
                        yyline, charLiteralStartCol, c); }
    \\\"\'           { yybegin(YYINITIAL);
                      char c = '\"';
                      return symbol("character " + c, sym.CHAR_LIT,
                        yyline, charLiteralStartCol, c); }
    \\.\'           { yybegin(YYINITIAL);
                      String message = "invalid escape character";
                      return symbol(
              errorString + message, sym.ERROR, yyline, charLiteralStartCol,
              message); }
    [^]             { yybegin(YYINITIAL);
                      String message = "invalid character";
                      return symbol(
              errorString + message, sym.ERROR, yyline, charLiteralStartCol,
              message); }
}

/* error fallback */
[^] { String message = "illegal symbol <" + yytext() + ">";
    return symbol (errorString + message, sym.ERROR, yyline, yycolumn,
     message); }
