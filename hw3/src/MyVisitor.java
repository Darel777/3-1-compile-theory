import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import types.ArrayType;
import types.BaseType;
import types.FunctionType;
import types.Type;

import java.util.ArrayList;
import java.util.List;

public class MyVisitor extends SysYParserBaseVisitor<Type>{

    int localIndex = 0;
    Scope globalScope;
    Scope nowScope;

    Symbol funcForCall = null;

    int nowisFunction = 0;

    int iscall = 0;

    Type nowretType=null;

    @Override
    public Type visitProgram(SysYParser.ProgramContext ctx) {
        globalScope = new Scope("GlobalScope",null);
        nowScope = globalScope;

        return visitChildren(ctx);
    }

    public Type visitConstArray(SysYParser.ConstArrayContext ctx) {
        if (checkIsSameDecl(ctx.IDENT().getText())){
            int lineNo = ctx.IDENT().getSymbol().getLine();
            printError(lineNo,3);
        }else {
            SysYParser.ConstDeclContext parentctx = (SysYParser.ConstDeclContext)ctx.getParent();
            Type type = null;
            if (parentctx.bType().getText().equals("int")){
                type = new BaseType("int");
            }
            int dimension = ctx.constExp().size();
            String name = ctx.IDENT().getText();
            ArrayType arraytype = new ArrayType(dimension,type);
            Symbol symbol = new Symbol(arraytype,name);
            nowScope.define(symbol);
        }
        //判断赋值类型问题
        int n = ctx.getChildCount();
        Symbol symbol = nowScope.resolve(ctx.IDENT().getText());
        for (int i = 0;i<n;i++){
            if (ctx.constInitVal().L_BRACE()!=null)break;
            ParseTree c = ctx.getChild(i);
            Type r = c.accept(this);
            if (i==n-1){
                //比较赋值的类型于自身的类型
                if (r!=null&&symbol!=null){
                    if (!r.compare(symbol.type)){
                        int lineNo = ctx.IDENT().getSymbol().getLine();
                        printError(lineNo,5);
                    }
                }
            }
        }

        return null;
    }

    @Override
    public Type visitConstVarable(SysYParser.ConstVarableContext ctx) {
        if (checkIsSameDecl(ctx.IDENT().getText())){
            int lineNo = ctx.IDENT().getSymbol().getLine();
            printError(lineNo,3);
        }else {
            SysYParser.ConstDeclContext parentctx = (SysYParser.ConstDeclContext)ctx.getParent();
            Type type = null;
            if (parentctx.bType().getText().equals("int")){
                type = new BaseType("int");
            }
            String name = ctx.IDENT().getText();
            Symbol symbol = new Symbol(type,name);
            nowScope.define(symbol);
        }
        int n = ctx.getChildCount();
        Symbol symbol = nowScope.resolve(ctx.IDENT().getText());
        for (int i = 0;i<n;i++){
            ParseTree c = ctx.getChild(i);
            Type r = c.accept(this);
            if (i==n-1){
                //比较赋值的类型于自身的类型
                if (r!=null&&symbol!=null){
                    if (!r.compare(symbol.type)){
                        int lineNo = ctx.IDENT().getSymbol().getLine();
                        printError(lineNo,5);
                    }
                }
            }
        }

        return null;
    }

    @Override
    public Type visitArray(SysYParser.ArrayContext ctx) {
        if (checkIsSameDecl(ctx.IDENT().getText())){
            int lineNo = ctx.IDENT().getSymbol().getLine();
            printError(lineNo,3);
        }else {
            SysYParser.VarDeclContext parentctx = (SysYParser.VarDeclContext)ctx.getParent();
            Type type = null;
            if (parentctx.bType().getText().equals("int")){
                type = new BaseType("int");
            }
            int dimension = ctx.constExp().size();
            String name = ctx.IDENT().getText();
            ArrayType arraytype = new ArrayType(dimension,type);
            Symbol symbol = new Symbol(arraytype,name);
            nowScope.define(symbol);
        }

        return super.visitArray(ctx);
    }

    @Override
    public Type visitAssignedArray(SysYParser.AssignedArrayContext ctx) {
        if (checkIsSameDecl(ctx.IDENT().getText())){
            int lineNo = ctx.IDENT().getSymbol().getLine();
            printError(lineNo,3);
        }else {
            SysYParser.VarDeclContext parentctx = (SysYParser.VarDeclContext)ctx.getParent();
            Type type = null;
            if (parentctx.bType().getText().equals("int")){
                type = new BaseType("int");
            }
            int dimension = ctx.constExp().size();
            String name = ctx.IDENT().getText();
            ArrayType arraytype = new ArrayType(dimension,type);
            Symbol symbol = new Symbol(arraytype,name);
            nowScope.define(symbol);
        }
        int n = ctx.getChildCount();
        Symbol symbol = nowScope.resolve(ctx.IDENT().getText());
        for (int i = 0;i<n;i++){
            if (ctx.initVal().L_BRACE()!=null)break;
            ParseTree c = ctx.getChild(i);
            Type r = c.accept(this);
            if (i==n-1){
                //比较赋值的类型于自身的类型
                if (r!=null&&symbol!=null){
                    if (!r.compare(symbol.type)){
                        int lineNo = ctx.IDENT().getSymbol().getLine();
                        printError(lineNo,5);
                    }
                }
            }
        }

        return null;
    }


    @Override
    public Type visitVarable(SysYParser.VarableContext ctx) {
        if (checkIsSameDecl(ctx.IDENT().getText())){
            int lineNo = ctx.IDENT().getSymbol().getLine();
            printError(lineNo,3);
        }else {
            SysYParser.VarDeclContext parentctx = (SysYParser.VarDeclContext)ctx.getParent();
            Type type = null;
            if (parentctx.bType().getText().equals("int")){
                type = new BaseType("int");
            }
            String name = ctx.IDENT().getText();
            Symbol symbol = new Symbol(type,name);
            nowScope.define(symbol);
        }

        return super.visitVarable(ctx);
    }

    @Override
    public Type visitAssignedVarable(SysYParser.AssignedVarableContext ctx) {
        if (checkIsSameDecl(ctx.IDENT().getText())){
            int lineNo = ctx.IDENT().getSymbol().getLine();
            printError(lineNo,3);
        }else {
            SysYParser.VarDeclContext parentctx = (SysYParser.VarDeclContext)ctx.getParent();
            Type type = null;
            if (parentctx.bType().getText().equals("int")){
                type = new BaseType("int");
            }
            String name = ctx.IDENT().getText();
            Symbol symbol = new Symbol(type,name);
            nowScope.define(symbol);
        }
        int n = ctx.getChildCount();
        Symbol symbol = nowScope.resolve(ctx.IDENT().getText());
        for (int i = 0;i<n;i++){
            ParseTree c = ctx.getChild(i);
            Type r = c.accept(this);
            if (i==n-1){
                //比较赋值的类型于自身的类型
                if (r!=null&&symbol!=null){
                    if (!r.compare(symbol.type)){
                        int lineNo = ctx.IDENT().getSymbol().getLine();
                        printError(lineNo,5);
                    }
                }
            }
        }

        return null;
    }

    @Override
    public Type visitFuncDef(SysYParser.FuncDefContext ctx) {
        if (checkRedefineFunction(ctx.IDENT().getText())){
            int lineNo = ctx.IDENT().getSymbol().getLine();
            printError(lineNo,4);
            return null;
        } else {
            String name = ctx.IDENT().getText();
            Type retType = null;
            //得到返回类型
            if (ctx.funcType().getText().equals("int")){
                retType = new BaseType("int");
            }else {
                retType = new BaseType("void");
            }
            nowretType = retType;
            //得到参数类型
            ArrayList<Type> ptypes = new ArrayList<>();
            if (ctx.funcFParams()!=null){
                for (int i = 0;i<ctx.funcFParams().funcFParam().size();i++){
                    String p = ctx.funcFParams().funcFParam(i).getText();
                    if (p.contains("[")){
                        ptypes.add(new ArrayType(1,new BaseType("int")));
                    }else {
                        ptypes.add(new BaseType("int"));
                    }
                }
            }

            FunctionType functionType = new FunctionType(retType,ptypes);
            Symbol symbol = new Symbol(functionType,name);
            nowScope.define(symbol);
            Scope funcScope = new Scope(name,nowScope);
            nowScope.addChildScope(funcScope);
            nowScope = funcScope;
            nowisFunction = 1;
        }

        return super.visitFuncDef(ctx);
    }



    @Override
    public Type visitFuncFParam(SysYParser.FuncFParamContext ctx) {
        if (checkIsSameDecl(ctx.IDENT().getText())){
            int lineNo = ctx.IDENT().getSymbol().getLine();
            printError(lineNo,3);
        }else {
            Type type = null;
            if (ctx.bType().getText().equals("int")&&ctx.getText().contains("[")){
                type = new ArrayType(1,new BaseType("int"));
            }else {
                type = new BaseType("int");
            }
            String name = ctx.IDENT().getText();
            Symbol symbol = new Symbol(type,name);
            nowScope.define(symbol);
        }

        return super.visitFuncFParam(ctx);
    }


    @Override
    public Type visitChildren(RuleNode node) {
        Type result = defaultResult();
        int n = node.getChildCount();
        for (int i = 0;i<n;i++){
            if (!shouldVisitNextChild(node,result)){
                break;
            }
            ParseTree c = node.getChild(i);
            Type childResult = c.accept(this);
            if (childResult!=null){
                result = childResult;
            }
        }
        return result;
    }

    @Override
    public Type visitTerminal(TerminalNode node) {
        String content = node.getText();
        int type = node.getSymbol().getType();
        String symbolname = SysYParser.VOCABULARY.getSymbolicName(type);
        if (content.equals("}")){
            RuleNode parent = (RuleNode) node.getParent();
            //如果是block的右大括号
            if (parent.getRuleContext().getRuleIndex()==14) {
                if (nowScope.fatherScope!=null){
                    nowScope = nowScope.fatherScope;
                }

            }
        }else if (symbolname.equals("INTEGR_CONST")){
            return new BaseType("int");
        } else if (symbolname.equals("IDENT")){
            if (funcForCall!=null){

                Symbol symbol = nowScope.resolve(content);
                if (symbol!=null){
                    int value = symbol.type.value;

                    if (value==2&&funcForCall!=null){
                        RuleNode parent = (RuleNode) node.getParent();
                        if (parent.getRuleContext().getRuleIndex()==SysYParser.RULE_lVal){
                            System.err.println("Error type "+8+" at Line "+node.getSymbol().getLine()+":");
                            Jishu.haserror = 1;
                            return null;
                        }
                        return ((FunctionType)symbol.type).returnType;
                    }else {
                        return symbol.type;
                    }
                }else {

                    System.err.println("Error type "+1+" at Line "+node.getSymbol().getLine()+":");
                    Jishu.haserror = 1;
                    return null;
                }

            }else {
                Symbol symbol = nowScope.resolve(content);
                if (symbol==null&&iscall == 0){
                    System.err.println("Error type "+1+" at Line "+node.getSymbol().getLine()+":");
                    Jishu.haserror = 1;
                    return null;
                }else if (symbol!=null){
                    int value = symbol.type.value;
                    if (value==2&&funcForCall!=null){
                        return ((FunctionType)symbol.type).returnType;
                    }else {
                        return symbol.type;
                    }

                }
            }
        }
        return null;
    }


        @Override
    public Type visitBlock(SysYParser.BlockContext ctx) {
        if (nowisFunction == 0){
           Scope localScope = new Scope("LocalScope"+localIndex,nowScope);
           localIndex++;
            nowScope.addChildScope(localScope);
            nowScope = localScope;
            visitChildren(ctx);
        }
        if (nowisFunction == 1){
            nowisFunction = 0;
            visitChildren(ctx);
            nowretType = null;
        }


        return null;
    }
//
//
    @Override
    public Type visitFunctionCall(SysYParser.FunctionCallContext ctx) {
        ArrayList<String> params = new ArrayList<>();
        //这一部分要处理几个错误
        //首先检查是否对变量进行了函数调用
        if (checkIsCallVara(ctx.IDENT().getText())){
            int lineNo = ctx.IDENT().getSymbol().getLine();
            printError(lineNo,10);
        }
        //接下来是调用未定义函数
        else if (checkIsUndefFunc(ctx.IDENT().getText())){
            int lineNo = ctx.IDENT().getSymbol().getLine();
            printError(lineNo,2);
        }else {
            funcForCall = (Symbol) globalScope.resolve(ctx.IDENT().getText());
            int size1 = ((FunctionType) funcForCall.type).paramsType.size();
            int size2 = ctx.funcRParams() == null?0:ctx.funcRParams().param().size();
            if (size2!=size1){
                int lineNo = ctx.IDENT().getSymbol().getLine();
                printError(lineNo,8);
            }
        }

        iscall = 1;
        Type res = visitChildren(ctx);
        iscall = 0;
        funcForCall = null;
        return res;
    }
//
    @Override
    public Type visitFuncRParams(SysYParser.FuncRParamsContext ctx) {
        Type result = defaultResult();
        if (funcForCall!=null){

            FunctionType fType = (FunctionType) funcForCall.type;
            result = fType.returnType;
            List<Type> params = fType.paramsType;
            if (params.size() == ctx.param().size()){
                int n = ctx.getChildCount();

                for (int i = 0;i<n;i++){
                    ParseTree c = ctx.getChild(i);
                    Type cjildResult = c.accept(this);
                    if (i%2 == 0){
                        if (cjildResult!=null&&!cjildResult.compare(params.get(i/2))){
                            int lineNo = ctx.start.getLine();
                            printError(lineNo,8);
                            funcForCall = null;
                            return null;
                        }
                    }

                }
            }



//                    int lineNo = ctx.start.getLine();
//                    printError(lineNo,8);
                funcForCall = null;
        }

        return result;
    }

    @Override
    public Type visitReturn_stmt(SysYParser.Return_stmtContext ctx) {
        Type retType = visitChildren(ctx);
        //不是void
        if (ctx.exp()!=null&&!nowretType.compare(retType)){
            int lineNo = ctx.start.getLine();
            printError(lineNo,7);
        }
        if (ctx.exp() == null && !((BaseType)nowretType).intOrVoid.equals("void")){
            int lineNo = ctx.start.getLine();
            printError(lineNo,7);
        }
        return null;
    }

    @Override
    public Type visitAssign_stmt(SysYParser.Assign_stmtContext ctx) {
        Type t1=null,t2=null;
        int n = ctx.getChildCount();
        for (int i = 0;i<n;i++){
            ParseTree c = ctx.getChild(i);
            Type res = c.accept(this);
            if (i == 0){
                t1 = res;
            }
            if (i==2){
                t2=res;
            }
        }

        if (t1==null || t2==null)return null;
        //检查赋值左侧类型是否为函数
        if (t1.value == 2){
            int lineNo = ctx.start.getLine();
            printError(lineNo,11);
            return null;
        }
        //检查赋值类型错误
        if (!t1.compare(t2)){
            int lineNo = ctx.start.getLine();
            printError(lineNo,5);
            return null;
        }


        return null;
    }

    @Override
    public Type visitLVal(SysYParser.LValContext ctx) {
        Type type = null;
        Symbol symbol = nowScope.resolve(ctx.IDENT().getText());

        int demision = ctx.L_BRACKT().size();
        //下面处理数组情况
        if (demision!= 0){
            if (symbol!=null){
                if (symbol.type.value!=3){
                    int lineNo = ctx.start.getLine();
                    printError(lineNo,9);
                    return null;
                }else {
                    ArrayType thetype = (ArrayType) symbol.type;
                    int theDemision = thetype.dimension;
                    int thisde = theDemision-demision;
                    if (thisde<0){
                        int lineNo = ctx.start.getLine();
                        printError(lineNo,9);
                        return null;
                    }else if (thisde == 0){
                        type = new BaseType("int");
                        return type;
                    } else if (thisde>0) {
                        type = new ArrayType(thisde,new BaseType("int"));
                        return type;

                    }
                }
            }
        }

//        if (ctx.getParent().getText().contains("=")&&ctx.parent.getRuleIndex()==SysYParser.RULE_exp
//        &&symbol!=null&&symbol.type.value == 2){
//            int lineNo = ctx.start.getLine();
//            printError(lineNo,11);
//            return null;
//        }

        int n = ctx.getChildCount();
        Type res = null;
        for (int i = 0;i<n;i++){
            ParseTree c = ctx.getChild(i);
            Type r = c.accept(this);
            if (i == 0)res = r;
        }


        return res;
    }

    @Override
    public Type visitMDMOP(SysYParser.MDMOPContext ctx) {
        int n = ctx.getChildCount();
        Type t1=null,t2=null;
        for (int i = 0;i<n;i++){
            ParseTree c = ctx.getChild(i);
            Type r = c.accept(this);
            if (i == 0){
                t1 = r;
            }
            if (i == 2){
                t2 = r;
            }
        }
        if (t1 == null||t2==null){return null;}
        if (t1.value!=1||t2.value!=1){
            int lineNo = ctx.start.getLine();
            printError(lineNo,6);
            return null;
        }else if (!((BaseType)t1).intOrVoid.equals("int")||!((BaseType)t2).intOrVoid.equals("int")){
            int lineNo = ctx.start.getLine();
            printError(lineNo,6);
            return null;
        }else {
            return t1;
        }
    }

    @Override
    public Type visitPMOP(SysYParser.PMOPContext ctx) {
        int n = ctx.getChildCount();
        Type t1=null,t2=null;
        for (int i = 0;i<n;i++){
            ParseTree c = ctx.getChild(i);
            Type r = c.accept(this);
            if (i == 0){
                t1 = r;
            }
            if (i == 2){
                t2 = r;
            }
        }
        if (t1 == null||t2==null){return null;}
        if (t1.value!=1||t2.value!=1){
            int lineNo = ctx.start.getLine();
            printError(lineNo,6);
            return null;
        }else if (!((BaseType)t1).intOrVoid.equals("int")||!((BaseType)t2).intOrVoid.equals("int")){
            int lineNo = ctx.start.getLine();
            printError(lineNo,6);
            return null;
        }else {
            return t1;
        }
    }

    @Override
    public Type visitUNOP(SysYParser.UNOPContext ctx) {
        int n = ctx.getChildCount();
        Type t1=null;
        for (int i = 0;i<n;i++){
            ParseTree c = ctx.getChild(i);
            Type r = c.accept(this);
            if (i == 1){
                t1 = r;
            }
        }
        if (t1 == null){return null;}
        if (t1.value!=1){
            int lineNo = ctx.start.getLine();
            printError(lineNo,6);
            return null;
        }else if (!((BaseType)t1).intOrVoid.equals("int")){
            int lineNo = ctx.start.getLine();
            printError(lineNo,6);
            return null;
        }else {
            return t1;
        }
    }

    @Override
    public Type visitLGLG(SysYParser.LGLGContext ctx) {
        int n = ctx.getChildCount();
        Type t1=null,t2=null;
        for (int i = 0;i<n;i++){
            ParseTree c = ctx.getChild(i);
            Type r = c.accept(this);
            if (i == 0){
                t1 = r;
            }
            if (i == 2){
                t2 = r;
            }
        }
        if (t1 == null||t2==null){return null;}
        if (t1.value!=1||t2.value!=1){
            int lineNo = ctx.start.getLine();
            printError(lineNo,6);
            return null;
        }else if (!((BaseType)t1).intOrVoid.equals("int")||!((BaseType)t2).intOrVoid.equals("int")){
            int lineNo = ctx.start.getLine();
            printError(lineNo,6);
            return null;
        }else {
            return t1;
        }
    }

    @Override
    public Type visitENEQ(SysYParser.ENEQContext ctx) {
        int n = ctx.getChildCount();
        Type t1=null,t2=null;
        for (int i = 0;i<n;i++){
            ParseTree c = ctx.getChild(i);
            Type r = c.accept(this);
            if (i == 0){
                t1 = r;
            }
            if (i == 2){
                t2 = r;
            }
        }
        if (t1 == null||t2==null){return null;}
        if (t1.value!=1||t2.value!=1){
            int lineNo = ctx.start.getLine();
            printError(lineNo,6);
            return null;
        }else if (!((BaseType)t1).intOrVoid.equals("int")||!((BaseType)t2).intOrVoid.equals("int")){
            int lineNo = ctx.start.getLine();
            printError(lineNo,6);
            return null;
        }else {
            return t1;
        }
    }

    @Override
    public Type visitCondAND(SysYParser.CondANDContext ctx) {
        int n = ctx.getChildCount();
        Type t1=null,t2=null;
        for (int i = 0;i<n;i++){
            ParseTree c = ctx.getChild(i);
            Type r = c.accept(this);
            if (i == 0){
                t1 = r;
            }
            if (i == 2){
                t2 = r;
            }
        }
        if (t1 == null||t2==null){return null;}
        if (t1.value!=1||t2.value!=1){
            int lineNo = ctx.start.getLine();
            printError(lineNo,6);
            return null;
        }else if (!((BaseType)t1).intOrVoid.equals("int")||!((BaseType)t2).intOrVoid.equals("int")){
            int lineNo = ctx.start.getLine();
            printError(lineNo,6);
            return null;
        }else {
            return t1;
        }
    }

    @Override
    public Type visitCondOR(SysYParser.CondORContext ctx) {
        int n = ctx.getChildCount();
        Type t1=null,t2=null;
        for (int i = 0;i<n;i++){
            ParseTree c = ctx.getChild(i);
            Type r = c.accept(this);
            if (i == 0){
                t1 = r;
            }
            if (i == 2){
                t2 = r;
            }
        }
        if (t1 == null||t2==null){return null;}
        if (t1.value!=1||t2.value!=1){
            int lineNo = ctx.start.getLine();
            printError(lineNo,6);
            return null;
        }else if (!((BaseType)t1).intOrVoid.equals("int")||!((BaseType)t2).intOrVoid.equals("int")){
            int lineNo = ctx.start.getLine();
            printError(lineNo,6);
            return null;
        }else {
            return t1;
        }
    }
    //错误1已完成

    //检查错误2
    boolean checkIsUndefFunc(String name){
        //检查全局作用域
        int size=globalScope.symbols.size();
        if (globalScope.symbols.get(name) == null){
            return true;
        }
        return false;

    }
    //对应错误3，检查重复定义变量问题
    boolean checkIsSameDecl(String name){
        int size=nowScope.symbols.size();
        for (int i = 0;i<size;i++){
            if (nowScope.symbols.get(name)!=null){
                Jishu.haserror = 1;
                return true;
            }
        }
        return false;

    }

    //对应错误4，检查重定义函数
    boolean checkRedefineFunction(String name){
        int size=nowScope.symbols.size();
        if (globalScope.symbols.get(name) != null){
            Jishu.haserror = 1;
            return true;
        }
        return false;
    }

    //对应错误8，检查函数参数适用
    boolean checkWrongParam(ArrayList<String> params, FunctionType functionType){
        for (int i = 0;i<params.size();i++){
            String thep = params.get(i);
            Type thistype = null;
            //变量
            if (!thep.contains("(")&&(thep.charAt(0) == '_'||(thep.charAt(0) >= 'a'&&thep.charAt(0) <= 'z')||(thep.charAt(0) >= 'A'&&thep.charAt(0) <= 'Z'))){
                Symbol symbol = nowScope.resolve(thep);
                thistype = symbol.type;
                System.out.println(thep);
            }else if ((thep.charAt(0) == '_'||(thep.charAt(0) >= 'a'&&thep.charAt(0) <= 'z')||(thep.charAt(0) >= 'A'&&thep.charAt(0) <= 'Z'))&&thep.contains("(")){
                int dex = thep.indexOf('(');

            }
        }
        return false;
    }

    //对应错误10，对变量进行函数调用
    boolean checkIsCallVara(String name){
        Scope sss = nowScope;
        while (sss!= null){
            if (sss.symbols.get(name)!= null){
                if (sss.symbols.get(name).type.value == 1 || sss.symbols.get(name).type.value == 3){
                    return true;
                }
            }
            sss = sss.fatherScope;
        }
        return false;
    }

    //工具函数：从当前作用域开始查找symbol

    void printError(int LineNO,int errorType){
        System.err.println("Error type "+errorType+" at Line "+LineNO+":");
        Jishu.haserror = 1;
    }

}
