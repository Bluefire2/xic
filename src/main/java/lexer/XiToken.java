package lexer;

import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;
import org.apache.commons.lang3.StringEscapeUtils;

public class XiToken extends ComplexSymbolFactory.ComplexSymbol {

    private String name;
    private XiTokenLocation location;
    public int parse_state;

    XiToken(String name, int id, Symbol left, Symbol right, Object value) {
        super(name, id, left, right, value);
        this.name = name;
        location = ((XiToken) left).location;
    }

    XiToken(String name, int id, Symbol left, Object value) {
        super(name, id, left, left, value);
        this.name = name;
        location = ((XiToken) left).location;
    }

    XiToken(String name, int id, XiTokenLocation location, Object value) {
        super(name, id, location, location, value);
        this.name = name;
        this.location = location;
    }

    XiToken(String name, int id) {
        super(name, id);
        this.name = name;
    }

    XiToken(String name, int id, Object value) {
        super(name, id, value);
        this.name = name;
    }

    XiToken(String name, int id, int state) {
        super(name, id, state);
        this.name = name;
        this.parse_state = state;
    }

    public XiTokenLocation getLocation() {
        return location;
    }

    public Object getValue() {
        switch (this.sym) {
            case xi_parser.sym.STRING_LIT:
            case xi_parser.sym.CHAR_LIT:
                return StringEscapeUtils.escapeJava(value.toString());
            default: return value;
        }
    }

    public String getName() {
        switch (this.sym) {
            case xi_parser.sym.STRING_LIT:
            case xi_parser.sym.CHAR_LIT:
                return StringEscapeUtils.escapeJava(name);
            default: return name;
        }
    }

    public String toString() {
        return getLeft().toString() + " " + getName();
    }
}