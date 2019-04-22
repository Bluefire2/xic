package kc875.lexer;

import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;

public class XiTokenFactory extends ComplexSymbolFactory {

    public XiTokenFactory() {}

    // Only this function is used by our lexer, the others are there to
    // formally extend ComplexSymbolFactory
    public XiToken newSymbol(String name, int id, XiTokenLocation left,
                            Object value) {
        return new XiToken(name, id, left, value);
    }

    public XiToken newSymbol(String name, int id, Symbol left, Symbol right,
                            Object value) {
        return new XiToken(name, id, left, right, value);
    }

    public XiToken newSymbol(String name, int id, Symbol left, Object value) {
        return new XiToken(name, id, left, value);
    }

    public XiToken newSymbol(String name, int id, Symbol left, Symbol right) {
        return new XiToken(name, id, left, right);
    }

    public XiToken newSymbol(String name, int id, Object value) {
        return new XiToken(name, id, value);
    }

    public XiToken newSymbol(String name, int id) {
        return new XiToken(name, id);
    }

    public XiToken startSymbol(String name, int id, int state) {
        return new XiToken(name, id, state);
    }
}
