package types;

public class BaseType extends Type{
    public String intOrVoid;
    public BaseType(String intOrVoid) {
        value = 1;
        this.intOrVoid = intOrVoid;
    }

    @Override
    public boolean compare(Type type) {
        if (this.value!=type.value)return false;
        BaseType baseType = (BaseType) type;
        if (!this.intOrVoid.equals(baseType.intOrVoid))return false;
        return true;
    }
}
