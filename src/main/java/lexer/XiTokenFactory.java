package lexer;

import java_cup.runtime.Symbol;
import java_cup.runtime.SymbolFactory;

public class XiTokenFactory implements SymbolFactory {

    @Override
    public Symbol newSymbol(String s, int i, Symbol symbol, Symbol symbol1, Object o) {
        return newSymbol(s,i,o);
    }

    @Override
    public Symbol newSymbol(String s, int i, Symbol symbol, Symbol symbol1) {
        return new XiToken(TokenType.valueOf(s), 0, 0,  null);
    }

    @Override
    public Symbol newSymbol(String s, int i, Symbol symbol, Object o) {
        return newSymbol(s,i,o);
    }

    @Override
    public Symbol newSymbol(String s, int i, Object o) {
        return new XiToken(TokenType.valueOf(s), 0, 0,  o);
    }

    @Override
    public Symbol newSymbol(String s, int i) {
        return new XiToken(TokenType.valueOf(s), 0, 0,  null);
    }

    @Override
    public Symbol startSymbol(String s, int i, int i1) {
        return new XiToken(TokenType.valueOf(s), 0, 0,  null);
    }
}
