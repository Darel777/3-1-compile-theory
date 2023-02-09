package types;

import java.util.ArrayList;

public class FunctionType extends Type{
    public Type returnType;
    public ArrayList<Type> paramsType = new ArrayList<>();

    public FunctionType(Type retType, ArrayList<Type> types){
        value = 2;
        if (retType!= null){
            this.returnType = retType;
        }else {
            this.returnType = null;
        }
        for (int i = 0;i<types.size();i++){
            paramsType.add(types.get(i));
        }
    }

    @Override
    public boolean compare(Type type) {
        if (this.value!=type.value)return false;
        FunctionType functionType = (FunctionType) type;
        if (!this.returnType.compare(functionType.returnType))return false;
        if (functionType.paramsType.size()!=this.paramsType.size())return false;
        for (int i = 0;i<this.paramsType.size();i++){
            if (!paramsType.get(i).compare(functionType.paramsType.get(i)))return false;
        }
        return true;
    }
}
