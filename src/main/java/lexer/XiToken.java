package lexer;
import java_cup.runtime.*;

public class XiToken extends Symbol {

    private String name;
    private XiTokenLocation tLeft;
    private XiTokenLocation tRight;

    XiToken(String name, int id, Symbol left, Symbol right, Object value) {
        super(id, left.left, right.right, value);
        this.name = name;
        tLeft = ((XiToken) left).tLeft;
        tRight = ((XiToken) right).tRight;
    }

    XiToken(String name, int id, Symbol left, Object value) {
        super(id, left.left, left.left, value);
        this.name = name;
        tLeft = ((XiToken) left).tLeft;
        tRight = ((XiToken) left).tLeft;
    }

    XiToken(String name, int id, XiTokenLocation left, XiTokenLocation right,
            Object value) {
        // left and right to super constructor doesn't matter here
        super(id, value);
        this.name = name;
        tLeft = left;
        tRight = right;
    }

    XiToken(String name, int id, XiTokenLocation left, Object value) {
        // left and right to super constructor doesn't matter here
        super(id, value);
        this.name = name;
        tLeft = left;
        tRight = left;
    }

    XiToken(String name, int id, XiTokenLocation left, XiTokenLocation right) {
        super(id, -1, -1);
        this.name = name;
        this.tLeft = left;
        this.tRight = right;
    }

    XiToken(String name, int id) {
        super(id);
        this.name = name;
    }

    XiToken(String name, int id, Object value) {
        super(id, value);
        this.name = name;
    }

    XiToken(String name, int id, int state) {
        super(id, state);
        this.name = name;
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