package ast;
import java.util.List;

public class MetaTypeList implements MetaType {
    List<Type> types;

    public MetaTypeList(List<Type> types) {
        this.types = types;
    }

    public List<Type> getTypes() {
        return types;
    }

    public int getLength(){
        return types.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MetaTypeList){
            MetaTypeList other = (MetaTypeList) obj;
            if (types.size() != other.getLength()){
                return false;
            }
            for (int i = 0; i < types.size(); i++){
                if (! (types.get(i).equals(other.getTypes().get(i)))){
                    return false;
                }
            }
        }
        return true;
    }
}
