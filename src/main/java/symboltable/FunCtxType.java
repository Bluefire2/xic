package symboltable;

import ast.*;
import java.util.List;

public class FunCtxType extends CtxType {
    private MetaType inputs;
    private MetaType outputs;

    public FunCtxType(MetaType inputs, MetaType outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public MetaType getInputs() {
        return inputs;
    }

    public MetaType getOutputs() {
        return outputs;
    }
}
