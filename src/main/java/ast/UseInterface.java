package ast;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import java_cup.runtime.Symbol;
import lexer.XiToken;

public class UseInterface implements ASTNode {
    private String name;

    private XiToken token;// Lexed token
    private int left;
    private int right;

    public UseInterface(String name, Symbol s) {
        token = (XiToken) s;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void prettyPrint(CodeWriterSExpPrinter w) {
        w.startList();
        w.printAtom("use");
        w.printAtom(name);
        w.endList();
    }

    @Override
    public void accept(VisitorAST visitor) throws ASTException {
        visitor.visit(this);
    }

    @Override
    public XiToken getToken() {
        return token;
    }
}
