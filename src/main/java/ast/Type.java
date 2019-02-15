package ast;

import xi_parser.Printable;

public abstract class Type implements Printable {
    public TypeType t_type;

    public TypeType getT_type() {
        return this.t_type;
    }
}
