import org.antlr.v4.runtime.tree.TerminalNode;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.LLVMValueRef;
import org.bytedeco.llvm.LLVM.*;
import static org.bytedeco.llvm.global.LLVM.*;
public class LLVMVisitor extends SysYParserBaseVisitor<LLVMValueRef>{

    public Scope globalScope;
    public Scope currentScope;
    LLVMValueRef currentFunction;
    LLVMModuleRef module;
    LLVMBuilderRef builder;
    LLVMTypeRef i32Type = LLVMInt32Type();
    LLVMTypeRef voidType = LLVMVoidType();
    boolean isFunc = false;
    int localNum = 0;
    boolean isGlobal = true;
    @Override
    public LLVMValueRef visitUNOP(SysYParser.UNOPContext ctx) {
        String u = ctx.unaryOp().getText();
        LLVMValueRef value = ctx.exp().accept(this);
        if (u.equals("+")){
            return value;
        }else if (u.equals("-")){
            return LLVMBuildNeg(builder,value,"neg");
        }else if (u.equals("!")){
            LLVMValueRef iszero = LLVMBuildICmp(builder, LLVMIntEQ, value, LLVMConstInt(i32Type, 0, 0), "c");
            return LLVMBuildZExt(builder, iszero, i32Type, "tmp_");
        }
        return super.visitUNOP(ctx);
    }
    @Override
    public LLVMValueRef visitMDMOP(SysYParser.MDMOPContext ctx) {
        LLVMValueRef value1 = ctx.getChild(0).accept(this);
        String op = ctx.getChild(1).getText();
        LLVMValueRef value2 = ctx.getChild(2).accept(this);
        if (op.equals("*")){
            return LLVMBuildMul(builder,value1,value2,"m");
        }else if (op.equals("/")){
            return LLVMBuildSDiv(builder,value1,value2,"div");
        }else if (op.equals("%")){
            return LLVMBuildSRem(builder,value1,value2,"mod");
        }
        return super.visitMDMOP(ctx);
    }
    @Override
    public LLVMValueRef visitPMOP(SysYParser.PMOPContext ctx) {
        LLVMValueRef value1 = ctx.getChild(0).accept(this);
        String op = ctx.getChild(1).getText();
        LLVMValueRef value2 = ctx.getChild(2).accept(this);
        if (op.equals("+")){
            return LLVMBuildAdd(builder,value1,value2,"add");
        }else if (op.equals("-")){
            return LLVMBuildSub(builder,value1,value2,"dec");
        }
        return super.visitPMOP(ctx);
    }
    @Override
    public LLVMValueRef visitTerminal(TerminalNode node) {
        String content = node.getText();
        int type = node.getSymbol().getType();
        String symbolname = SysYParser.VOCABULARY.getSymbolicName(type);
        if (symbolname.equals("INTEGR_CONST")){
            int text = 0;
            if (content.length()>2){
                if (content.charAt(0) == '0' && (content.charAt(1) == 'x'|| content.charAt(1) == 'X')){
                    text = Integer.parseInt(content.substring(2),16);
                    content = String.valueOf(text);
                } else if (content.charAt(0) == '0' && content.charAt(1) >= '0' && content.charAt(1)<='7' ){
                    text = Integer.parseInt(content,8);content = String.valueOf(text);
                }else {
                    text = Integer.parseInt(content);
                }
            }else {
                text = Integer.parseInt(content);
            }
            return LLVMConstInt(i32Type,text,0);

        }
        return super.visitTerminal(node);
    }
    @Override
    public LLVMValueRef visitProgram(SysYParser.ProgramContext ctx) {
        globalScope = new Scope("globalScope",null);
        currentScope = globalScope;
        return super.visitProgram(ctx);
    }
    @Override
    public LLVMValueRef visitFuncDef(SysYParser.FuncDefContext ctx) {
        Scope funcScope = new Scope(ctx.IDENT().getText(),globalScope);
        globalScope.addChildScope(funcScope);
        LLVMTypeRef returnType;
        if (!ctx.funcType().getText().equals("void")){
            returnType = i32Type;
        }else {
            returnType = voidType;
        }

        PointerPointer<Pointer> argumentTypes;
        int argueCount;
        if (ctx.funcFParams()!=null){
            argueCount = ctx.funcFParams().funcFParam().size();
            argumentTypes = new PointerPointer<>(argueCount);
            if (argueCount>0){
                for (int i = 0;i<argueCount;i++){
                    argumentTypes.put(i,i32Type);
                }
            }
        }else {
            argueCount = 0;
            argumentTypes = new PointerPointer<>();
        }

        LLVMTypeRef ft = LLVMFunctionType(returnType, argumentTypes, /* argumentCount */ argueCount, /* isVariadic */ 0);
        LLVMValueRef function = LLVMAddFunction(module, /*functionName:String*/ctx.IDENT().getText(), ft);
        currentFunction = function;
        Symbol funcSymbol;
        if (ctx.funcType().getText().equals("int")){
            funcSymbol = new Symbol(function,ctx.IDENT().getText(),2);
        }else {
            funcSymbol = new Symbol(function,ctx.IDENT().getText(),3);
        }
        globalScope.define(funcSymbol);
        currentScope = funcScope;
        isGlobal = false;
        isFunc = true;
        LLVMBasicBlockRef block1 = LLVMAppendBasicBlock(function, /*blockName:String*/ctx.IDENT().getText()+"Entry");


        LLVMPositionBuilderAtEnd(builder, block1);
        for (int i = 0;i<argueCount;i++){
            LLVMValueRef param = ctx.funcFParams().funcFParam(i).accept(this);
            LLVMValueRef arg = LLVMGetParam(function,i);
            LLVMBuildStore(builder,arg,param);
            currentScope.define(new Symbol(param,ctx.funcFParams().funcFParam(i).IDENT().getText(),0));
        }
        visitBlock(ctx.block());
        if (ctx.funcType().getText().equals("void")){
            LLVMBuildRetVoid(builder);
        }
        currentScope = currentScope.fatherScope;
        isGlobal = true;

        return null;
    }
    @Override
    public LLVMValueRef visitFunctionCall(SysYParser.FunctionCallContext ctx) {
        LLVMValueRef calledFunc = currentScope.resolve(ctx.IDENT().getText()).llvmValueRef;
        int argCount = 0;
        PointerPointer<Pointer> argValue = new PointerPointer<>();
        if (ctx.funcRParams()!=null){
            argCount = ctx.funcRParams().param().size();
            argValue = new PointerPointer<>(argCount);
            for (int i = 0;i<argCount;i++){
                LLVMValueRef thevalue = ctx.funcRParams().param(i).accept(this);
                argValue.put(i,thevalue);
            }

        }
        if (currentScope.resolve(ctx.IDENT().getText()).type==3){
            return LLVMBuildCall(builder,calledFunc,argValue,argCount,"");
        }
        return LLVMBuildCall(builder,calledFunc,argValue,argCount,ctx.IDENT().getText());
    }
    @Override
    public LLVMValueRef visitLGLG(SysYParser.LGLGContext ctx) {
        LLVMValueRef first = ctx.getChild(0).accept(this);
        LLVMValueRef second = ctx.getChild(2).accept(this);
        String compare = ctx.getChild(1).getText();
        if (compare.equals(">")){
            LLVMValueRef int32First = LLVMBuildZExt(builder,first,i32Type,"toi32");
            LLVMValueRef cond = LLVMBuildICmp(builder,LLVMIntUGT,int32First,second,"greater");
            return cond;
        }else if (compare.equals("<")){
            LLVMValueRef int32First = LLVMBuildZExt(builder,first,i32Type,"toi32");
            LLVMValueRef cond = LLVMBuildICmp(builder,LLVMIntULT,int32First,second,"lesser");
            return cond;
        }else if (compare.equals(">=")){
            LLVMValueRef int32First = LLVMBuildZExt(builder,first,i32Type,"toi32");
            LLVMValueRef cond = LLVMBuildICmp(builder,LLVMIntUGE,int32First,second,"greaterEqual");
            return cond;
        }else if (compare.equals("<=")){
            LLVMValueRef int32First = LLVMBuildZExt(builder,first,i32Type,"toi32");
            LLVMValueRef cond = LLVMBuildICmp(builder,LLVMIntULE,int32First,second,"lesserEqual");
            return cond;
        }
        return null;
    }
    @Override
    public LLVMValueRef visitENEQ(SysYParser.ENEQContext ctx) {
        LLVMValueRef first = ctx.getChild(0).accept(this);
        LLVMValueRef second = ctx.getChild(2).accept(this);
        String compare = ctx.getChild(1).getText();
        if (compare.equals("==")){
            LLVMValueRef int32First = LLVMBuildZExt(builder,first,i32Type,"toi32");
            LLVMValueRef cond = LLVMBuildICmp(builder,LLVMIntEQ,int32First,second,"equal");
            return cond;
        }else if (compare.equals("!=")){
            LLVMValueRef int32First = LLVMBuildZExt(builder,first,i32Type,"toi32");
            LLVMValueRef cond = LLVMBuildICmp(builder,LLVMIntNE,int32First,second,"notEqual");
            return cond;
        }
        return null;
    }
    @Override
    public LLVMValueRef visitCondAND(SysYParser.CondANDContext ctx) {
        LLVMValueRef first = ctx.getChild(0).accept(this);
        LLVMValueRef second = ctx.getChild(2).accept(this);
        LLVMValueRef int1First = LLVMBuildZExt(builder,first,LLVMInt1Type(),"toi32");
        LLVMValueRef int1Second = LLVMBuildZExt(builder,first,LLVMInt1Type(),"toi32");
        LLVMValueRef cond = LLVMBuildAnd(builder,first,second,"AND");
        return cond;
    }
    @Override
    public LLVMValueRef visitCondOR(SysYParser.CondORContext ctx) {
        LLVMValueRef first = ctx.getChild(0).accept(this);
        LLVMValueRef second = ctx.getChild(2).accept(this);
        LLVMValueRef int1First = LLVMBuildZExt(builder,first,LLVMInt1Type(),"toi32");
        LLVMValueRef int1Second = LLVMBuildZExt(builder,first,LLVMInt1Type(),"toi32");
        LLVMValueRef cond = LLVMBuildOr(builder,first,second,"OR");
        return cond;
    }
    @Override
    public LLVMValueRef visitLVal(SysYParser.LValContext ctx) {
        Symbol theArg = currentScope.resolve(ctx.IDENT().getText());
        if (ctx.L_BRACKT().size()==0){
            return LLVMBuildLoad(builder,theArg.llvmValueRef,theArg.name);
        }else {
            LLVMValueRef index = ctx.exp(0).accept(this);
            LLVMValueRef p = LLVMBuildGEP(builder,theArg.llvmValueRef,new PointerPointer(new LLVMValueRef[]{LLVMConstInt(i32Type,0,0),index}),2,"pointer");
            return LLVMBuildLoad(builder,p,theArg.name);
        }

    }
    @Override
    public LLVMValueRef visitBlock(SysYParser.BlockContext ctx) {
        if (isFunc){
            isFunc = false;
            visitChildren(ctx);
        }else {
            Scope local = new Scope("LocalScope"+localNum,currentScope);
            localNum++;
            currentScope.addChildScope(local);
            currentScope = local;
            visitChildren(ctx);
            currentScope = currentScope.fatherScope;
        }

        return null;
    }
    @Override
    public LLVMValueRef visitArray(SysYParser.ArrayContext ctx) {
        if (!isGlobal){
            String name = ctx.IDENT().getText();
            LLVMValueRef thesize = ctx.constExp(0).accept(this);
            int size = (int)LLVMConstIntGetZExtValue(thesize);
            LLVMTypeRef arrayType = LLVMArrayType(i32Type,size);
            LLVMValueRef arrayPointer = LLVMBuildAlloca(builder,arrayType,name);
            currentScope.define(new Symbol(arrayPointer,name,1));
        }else {
            String name = ctx.IDENT().getText();
            LLVMValueRef thesize = ctx.constExp(0).accept(this);
            int size = (int)LLVMConstIntGetZExtValue(thesize);
            LLVMTypeRef arrayType = LLVMArrayType(i32Type,size);
            LLVMValueRef arrayPointer = LLVMAddGlobal(module,arrayType,name);
            PointerPointer<Pointer> p = new PointerPointer<>(size);
            for (int i = 0;i<size;i++){
                p.put(i,LLVMConstInt(i32Type,0,0));
            }
            LLVMValueRef v = LLVMConstArray(i32Type,p,size);
            LLVMSetInitializer(arrayPointer,v);
            globalScope.define(new Symbol(arrayPointer,name,1));
        }

        return null;
    }
    @Override
    public LLVMValueRef visitAssignedArray(SysYParser.AssignedArrayContext ctx) {
        if (!isGlobal){
            String name = ctx.IDENT().getText();
            LLVMValueRef thesize = ctx.constExp(0).accept(this);
            int size = (int)LLVMConstIntGetZExtValue(thesize);
            LLVMTypeRef arrayType = LLVMArrayType(i32Type,size);
            LLVMValueRef arrayPointer = LLVMBuildAlloca(builder,arrayType,name);
            int preSize = ctx.initVal().initVal().size();
            for (int i = 0;i<preSize;i++){
                LLVMValueRef p = LLVMBuildGEP(builder,arrayPointer,new PointerPointer(new LLVMValueRef[]{LLVMConstInt(i32Type,0,0),LLVMConstInt(i32Type,i,0)}),2,"pointer");
                LLVMBuildStore(builder,ctx.initVal().initVal(i).accept(this),p);
            }
            for (int i = preSize;i<size;i++){
                LLVMValueRef p = LLVMBuildGEP(builder,arrayPointer,new PointerPointer(new LLVMValueRef[]{LLVMConstInt(i32Type,0,0),LLVMConstInt(i32Type,i,0)}),2,"pointer");
                LLVMBuildStore(builder,LLVMConstInt(i32Type,0,0),p);
            }
            currentScope.define(new Symbol(arrayPointer,name,1));
        }else {
            String name = ctx.IDENT().getText();
            LLVMValueRef thesize = ctx.constExp(0).accept(this);
            int size = (int)LLVMConstIntGetZExtValue(thesize);
            LLVMTypeRef arrayType = LLVMArrayType(i32Type,size);
            LLVMValueRef arrayPointer = LLVMAddGlobal(module,arrayType,name);
            int preSize = ctx.initVal().initVal().size();
            PointerPointer<Pointer> p = new PointerPointer<>(size);
            for (int i = 0;i<preSize;i++){
                LLVMValueRef thevalue = ctx.initVal().initVal(i).accept(this);
                p.put(i,thevalue);
            }
            for (int i = preSize;i<size;i++){
                p.put(i,LLVMConstInt(i32Type,0,0));
            }
            LLVMValueRef v = LLVMConstArray(i32Type,p,size);
            LLVMSetInitializer(arrayPointer,v);
            globalScope.define(new Symbol(arrayPointer,name,1));

        }


        return null;
    }
    @Override
    public LLVMValueRef visitConstArray(SysYParser.ConstArrayContext ctx) {
        if (!isGlobal){
            String name = ctx.IDENT().getText();
            LLVMValueRef thesize = ctx.constExp(0).accept(this);
            int size = (int)LLVMConstIntGetZExtValue(thesize);
            LLVMTypeRef arrayType = LLVMArrayType(i32Type,size);
            LLVMValueRef arrayPointer = LLVMBuildAlloca(builder,arrayType,name);
            int preSize = ctx.constInitVal().constInitVal().size();
            for (int i = 0;i<preSize;i++){
                LLVMValueRef p = LLVMBuildGEP(builder,arrayPointer,new PointerPointer(new LLVMValueRef[]{LLVMConstInt(i32Type,0,0),LLVMConstInt(i32Type,i,0)}),2,"pointer");
                LLVMBuildStore(builder,ctx.constInitVal().constInitVal(i).accept(this),p);
            }
            for (int i = preSize;i<size;i++){
                LLVMValueRef p = LLVMBuildGEP(builder,arrayPointer,new PointerPointer(new LLVMValueRef[]{LLVMConstInt(i32Type,0,0),LLVMConstInt(i32Type,i,0)}),2,"pointer");
                LLVMBuildStore(builder,LLVMConstInt(i32Type,0,0),p);
            }
            currentScope.define(new Symbol(arrayPointer,name,1));
        }else {
            String name = ctx.IDENT().getText();
            LLVMValueRef thesize = ctx.constExp(0).accept(this);
            int size = (int)LLVMConstIntGetZExtValue(thesize);
            LLVMTypeRef arrayType = LLVMArrayType(i32Type,size);
            LLVMValueRef arrayPointer = LLVMAddGlobal(module,arrayType,name);
            int preSize = ctx.constInitVal().constInitVal().size();
            PointerPointer<Pointer> p = new PointerPointer<>(size);
            for (int i = 0;i<preSize;i++){
                LLVMValueRef thevalue = ctx.constInitVal().constInitVal(i).accept(this);
                p.put(i,thevalue);
            }
            for (int i = preSize;i<size;i++){
                p.put(i,LLVMConstInt(i32Type,0,0));
            }
            LLVMValueRef v = LLVMConstArray(i32Type,p,size);
            LLVMSetInitializer(arrayPointer,v);
            globalScope.define(new Symbol(arrayPointer,name,1));
        }


        return null;
    }
    @Override
    public LLVMValueRef visitVarable(SysYParser.VarableContext ctx) {
        if (!isGlobal){
            LLVMValueRef theparam = LLVMBuildAlloca(builder,i32Type,ctx.IDENT().getText());
            currentScope.define(new Symbol(theparam,ctx.IDENT().getText(),0));
        }else {
            LLVMValueRef globalVar = LLVMAddGlobal(module, i32Type, /*globalVarName:String*/ctx.IDENT().getText());
            globalScope.define(new Symbol(globalVar,ctx.IDENT().getText(),0));
        }
        return null;
    }
    @Override
    public LLVMValueRef visitAssignedVarable(SysYParser.AssignedVarableContext ctx) {
        if (!isGlobal){
            LLVMValueRef theparam = LLVMBuildAlloca(builder,i32Type,ctx.IDENT().getText());
            LLVMValueRef value = ctx.initVal().accept(this);
            LLVMBuildStore(builder,value,theparam);
            currentScope.define(new Symbol(theparam,ctx.IDENT().getText(),0));
        }else {
            LLVMValueRef globalVar = LLVMAddGlobal(module, i32Type, /*globalVarName:String*/ctx.IDENT().getText());
            LLVMValueRef value = ctx.initVal().accept(this);
            LLVMSetInitializer(globalVar, /* constantVal:LLVMValueRef*/value);
            globalScope.define(new Symbol(globalVar,ctx.IDENT().getText(),0));
        }

        return null;
    }
    @Override
    public LLVMValueRef visitAssign_stmt(SysYParser.Assign_stmtContext ctx) {
        Symbol lval = currentScope.resolve(ctx.lVal().IDENT().getText());
        LLVMValueRef arg = lval.llvmValueRef;
        LLVMValueRef value = ctx.exp().accept(this);
        if (lval.type == 0){
            return LLVMBuildStore(builder,value,arg);
        }else {
            LLVMValueRef index = ctx.lVal().exp(0).accept(this);
            LLVMValueRef p = LLVMBuildGEP(builder,arg,new PointerPointer(new LLVMValueRef[]{LLVMConstInt(i32Type,0,0),index}),2,"pointer");
            return LLVMBuildStore(builder,value,p);
        }
    }
    @Override
    public LLVMValueRef visitConstVarable(SysYParser.ConstVarableContext ctx) {
        if (!isGlobal){
            LLVMValueRef theparam = LLVMBuildAlloca(builder,i32Type,ctx.IDENT().getText());
            LLVMValueRef value = ctx.constInitVal().accept(this);
            LLVMBuildStore(builder,value,theparam);
            currentScope.define(new Symbol(theparam,ctx.IDENT().getText(),0));
        }else {
            LLVMValueRef globalVar = LLVMAddGlobal(module, i32Type, /*globalVarName:String*/ctx.IDENT().getText());
            LLVMValueRef value = ctx.constInitVal().accept(this);
            LLVMSetInitializer(globalVar, /* constantVal:LLVMValueRef*/value);
            globalScope.define(new Symbol(globalVar,ctx.IDENT().getText(),0));
        }

        return null;
    }
    @Override
    public LLVMValueRef visitFuncFParam(SysYParser.FuncFParamContext ctx) {
        String paramName = ctx.IDENT().getText();
        return LLVMBuildAlloca(builder,i32Type,paramName);
    }
    @Override
    public LLVMValueRef visitReturn_stmt(SysYParser.Return_stmtContext ctx) {
        if (ctx.exp()!=null){
            return LLVMBuildRet(builder,ctx.exp().accept(this));
        }
        return null;
    }
}
