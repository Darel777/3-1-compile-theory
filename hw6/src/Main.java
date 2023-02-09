import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.bytedeco.javacpp.BytePointer;
import java.io.IOException;
import org.bytedeco.llvm.LLVM.*;
import static org.bytedeco.llvm.global.LLVM.*;
public class Main {
    public static final BytePointer error = new BytePointer();
    public static void main(String[] args) throws IOException {
        //初始化LLVM
        LLVMInitializeCore(LLVMGetGlobalPassRegistry());
        LLVMLinkInMCJIT();
        LLVMInitializeNativeAsmPrinter();
        LLVMInitializeNativeAsmParser();
        LLVMInitializeNativeTarget();
        //创建module
        LLVMModuleRef module = LLVMModuleCreateWithName("module");
        //初始化IRBuilder，后续将使用这个builder去生成LLVM IR
        LLVMBuilderRef builder = LLVMCreateBuilder();
        CharStream input = CharStreams.fromFileName(args[0]);
        SysYLexer sysYLexer = new SysYLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(sysYLexer);
        SysYParser sysYParser = new SysYParser(tokens);
        ParseTree tree = sysYParser.program();
        LLVMVisitor llvmVisitor = new LLVMVisitor();
        llvmVisitor.module = module;
        llvmVisitor.builder = builder;
        llvmVisitor.currentScope=new Scope("GlobalScope",null);
        llvmVisitor.visit(tree);
        LLVMPrintModuleToFile(module,args[1],error);
    }
}