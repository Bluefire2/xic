package kc875.ast;

import java_cup.runtime.ComplexSymbolFactory;

import java.util.List;

/**
 * A FileSource AST for a .xi or .ixi file.
 */
abstract class FileSource extends ASTNode implements Printable {
    List<UseInterface> imports;

    FileSource(List<UseInterface> imports,
               ComplexSymbolFactory.Location location) {
        super(location);
        this.imports = imports;
    }

    public List<UseInterface> getImports() {
        return imports;
    }
}
