package lexer;
import java_cup.runtime.*;

public class XiToken extends ComplexSymbolFactory.ComplexSymbol {

    private String name;
    private XiTokenLocation tLeft;
    private XiTokenLocation tRight;
    public int parse_state;

    XiToken(String name, int id, Symbol left, Symbol right, Object value) {
        super(name, id, left, right, value);
        this.name = name;
        tLeft = ((XiToken) left).tLeft;
        tRight = ((XiToken) right).tRight;
    }

    XiToken(String name, int id, Symbol left, Object value) {
        super(name, id, left, value);
        this.name = name;
        tLeft = ((XiToken) left).tLeft;
        tRight = ((XiToken) left).tLeft;
    }

    XiToken(String name, int id, XiTokenLocation left, XiTokenLocation right,
            Object value) {
        // left and right to super constructor doesn't matter here
        super(name, id, value);
        this.name = name;
        tLeft = left;
        tRight = right;
    }

    XiToken(String name, int id, XiTokenLocation left, Object value) {
        // left and right to super constructor doesn't matter here
        super(name, id, value);
        this.name = name;
        tLeft = left;
        tRight = left;
    }

    XiToken(String name, int id, XiTokenLocation left, XiTokenLocation right) {
        super(name, id, left, right);
        this.name = name;
        this.tLeft = left;
        this.tRight = right;
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

    public XiTokenLocation getLeft() {
        return tLeft;
    }

    public XiTokenLocation getRight() {
        return tRight;
    }

    public Object getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return getLeft().toString() + " " + getName();
    }
}