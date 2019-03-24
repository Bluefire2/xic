package codegen;

public class ASMLabel extends ASMInstr {
    String name;

    ASMLabel(String n) {
        this.name = n;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString(){
        return name+":";
    }
}
