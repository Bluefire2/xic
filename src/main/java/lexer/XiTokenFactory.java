package lexer;

import java_cup.runtime.Symbol;
import java_cup.runtime.SymbolFactory;

public class XiTokenFactory implements SymbolFactory {

    public XiTokenFactory() {}

    public Symbol newSymbol(String name, int id, XiTokenLocation left,
                            XiTokenLocation right, Object value) {
        return new XiToken(name, id, left, right, value);
    }

    public Symbol newSymbol(String name, int id, XiTokenLocation left,
                            XiTokenLocation right) {
        return new XiToken(name, id, left, right);
    }

    public Symbol newSymbol(String name, int id, XiTokenLocation left,
                            Object value) {
        return new XiToken(name, id, left, value);
    }

    public Symbol newSymbol(String name, int id, Symbol left, Symbol right,
                            Object value) {
        return new XiToken(name, id, left, right, value);
    }

    public Symbol newSymbol(String name, int id, Symbol left, Object value) {
        return new XiToken(name, id, left, value);
    }

    public Symbol newSymbol(String name, int id, Symbol left, Symbol right) {
        return new XiToken(name, id, left, right);
    }

    public Symbol newSymbol(String name, int id, Object value) {
        return new XiToken(name, id, value);
    }

    public Symbol newSymbol(String name, int id) {
        return new XiToken(name, id);
    }

    public Symbol startSymbol(String name, int id, int state) {
        return new XiToken(name, id, state);
    }
}
