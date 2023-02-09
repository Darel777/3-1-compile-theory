import org.bytedeco.llvm.LLVM.LLVMValueRef;

public class Symbol {
    LLVMValueRef llvmValueRef;
    public String name;
    int type;
    public Symbol(LLVMValueRef llvmValueRef,String name,int type){
        this.llvmValueRef = llvmValueRef;
        this.name = name;
        this.type = type;
    }
}
