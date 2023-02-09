package types;

import java.util.ArrayList;

public class ArrayType extends Type{

    public int dimension;
    public Type type;

    public ArrayType( int dimension, Type type){
        value = 3;
        this.dimension = dimension;
        this.type = type;
    }

    @Override
    public boolean compare(Type type) {
        if (this.value!=type.value)return false;
        ArrayType arrayType = (ArrayType) type;
        if (!this.type.compare(arrayType.type)||this.dimension!=arrayType.dimension)return false;
        return true;
    }
}
