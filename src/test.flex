/* Example JFlex grammar file
 * The generated lexer class will have an API as specified here:
 * http://jflex.de/manual.html#ScannerMethods
**/

%%

/* the name of your lexer class */
%class XiLexer

%type XiToken

/* declare variables */
%{
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

    //list of reserved words (may be useful)
    private String[] reserved = {"use","if","while","else","return","length","bool","int","true","false"};

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

/* declare a new lexical state */
//TODO: INTEGER AND STRING AND CHARACTER LITERALS

/* macro */
EOL = \r|\n|\r\n
NON_EOL = [^\r\n]
WHITESPACE = {EOL} | [ \t\f]
HEX_DIGIT = [0-9A-Fa-f]
ID = [A-Za-z][A-Za-z0-9\'_]* //variables
COMMENT = "//"{NON_EOL}*{EOL}? //{EOL}? because comment may be last line in file
INTEGER = 0 | [1-9][0-9]*

%%

/* lexical rules */

// commented out the example code
//{ALPHA}  {
//  /* returns the matched text */
//  System.out.println(yytext());
//
//  /* returns the current line of input */
//  System.out.println(yyline);
//
//  /* returns the current column of the current line */
//  System.out.println(yycolumn);
//
//  return new XiToken()
//}
//
//{NUM} {
//  /* enter the FOO lexical state */
//  yybegin(FOO);
//
//  return SYM_NUM;
//}
//
///* declare rules only for the FOO state */
//<FOO> {
//  /* when matching a colon, increment `bar` and then return a token with index 0 */
//  ":" { bar++; return SYM_COLON; }
//}

<YYINITIAL> {
    {INTEGER} {return new XiToken(TokenType.INT_LIT, yyline, yycolumn, new Long(yytext()));}
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
    /* other */
    "_"  { return new XiToken(TokenType.UNDERSCORE, yyline, yycolumn, yytext());}
    {ID} { return new XiToken(TokenType.ID, yyline, yycolumn, yytext());}
    {WHITESPACE} {}//ignore
    {COMMENT} {}//ignore

    /* error fallback */
    [^] { throw new Error("Illegal character <"+yytext()+">");}
}




