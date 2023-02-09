//import org.antlr.v4.runtime.ParserRuleContext;
//import org.antlr.v4.runtime.tree.RuleNode;
//import org.antlr.v4.runtime.tree.TerminalNode;
//import types.ArrayType;
//import types.FunctionType;
//import types.BaseType;
//import types.Type;
//
//import java.util.ArrayList;
//
//public class TypeCheckVisitor<T> extends SysYParserBaseVisitor<T>{
//    public Scope scope;
//    public Scope nowScope;
//
//    Symbol funcForCall = null;
//
//
//    int nowisFunction = 0;
//    //全局
//    @Override
//    public T visitProgram(SysYParser.ProgramContext ctx) {
//        Scope Scope = new Scope(null);
//        this.scope = Scope;
//        this.nowScope = Scope;
//        return super.visitProgram(ctx);
//    }
//
//    //const数组
//    @Override
//    public T visitConstArray(SysYParser.ConstArrayContext ctx) {
//        if (checkIsSameDecl(ctx.IDENT().getText())){
//            int lineNo = ctx.IDENT().getSymbol().getLine();
//            printError(lineNo,3);
//        }else {
//            SysYParser.ConstDeclContext parentctx = (SysYParser.ConstDeclContext)ctx.getParent();
//            Type type = null;
//            if (parentctx.bType().getText().equals("int")){
//                type = new BaseType();
//            }
//            int dimension = ctx.constExp().size();
//
//            ArrayList<Integer> counts = new ArrayList<>();
//            for (int i = 0;i<dimension;i++){
//                counts.add(Integer.parseInt(ctx.constExp(i).getText()));
//            }
//            String name = ctx.IDENT().getText();
//            ArrayType arraytype = new ArrayType(counts,dimension,type);
//            ArraySymbol symbol = new ArraySymbol(name,arraytype);
//            nowScope.define(symbol);
//        }
//
//        return super.visitConstArray(ctx);
//    }
//
//    //const变量
//    @Override
//    public T visitConstVarable(SysYParser.ConstVarableContext ctx) {
//        if (checkIsSameDecl(ctx.IDENT().getText())){
//            int lineNo = ctx.IDENT().getSymbol().getLine();
//            printError(lineNo,3);
//        }else {
//            SysYParser.ConstDeclContext parentctx = (SysYParser.ConstDeclContext)ctx.getParent();
//            Type type = null;
//            if (parentctx.bType().getText().equals("int")){
//                type = new BaseType();
//            }
//            String name = ctx.IDENT().getText();
//            IntSymbol symbol = new IntSymbol(name,type);
//            nowScope.define(symbol);
//        }
//
//        return super.visitConstVarable(ctx);
//    }
//
//    @Override
//    public T visitArray(SysYParser.ArrayContext ctx) {
//        if (checkIsSameDecl(ctx.IDENT().getText())){
//            int lineNo = ctx.IDENT().getSymbol().getLine();
//            printError(lineNo,3);
//        }else {
//            SysYParser.VarDeclContext parentctx = (SysYParser.VarDeclContext)ctx.getParent();
//            Type type = null;
//            if (parentctx.bType().getText().equals("int")){
//                type = new BaseType();
//            }
//            int dimension = ctx.constExp().size();
//
//            ArrayList<Integer> counts = new ArrayList<>();
//            for (int i = 0;i<dimension;i++){
//                counts.add(Integer.parseInt(ctx.constExp(i).getText()));
//            }
//            String name = ctx.IDENT().getText();
//            ArrayType arraytype = new ArrayType(counts,dimension,type);
//            ArraySymbol symbol = new ArraySymbol(name,arraytype);
//            nowScope.define(symbol);
//        }
//
//        return super.visitArray(ctx);
//    }
//
//    @Override
//    public T visitAssignedArray(SysYParser.AssignedArrayContext ctx) {
//        if (checkIsSameDecl(ctx.IDENT().getText())){
//            int lineNo = ctx.IDENT().getSymbol().getLine();
//            printError(lineNo,3);
//        }else {
//            SysYParser.VarDeclContext parentctx = (SysYParser.VarDeclContext)ctx.getParent();
//            Type type = null;
//            if (parentctx.bType().getText().equals("int")){
//                type = new BaseType();
//            }
//            int dimension = ctx.constExp().size();
//
//            ArrayList<Integer> counts = new ArrayList<>();
//            for (int i = 0;i<dimension;i++){
//                counts.add(Integer.parseInt(ctx.constExp(i).getText()));
//            }
//            String name = ctx.IDENT().getText();
//            ArrayType arraytype = new ArrayType(counts,dimension,type);
//            ArraySymbol symbol = new ArraySymbol(name,arraytype);
//            nowScope.define(symbol);
//        }
//
//        return super.visitAssignedArray(ctx);
//    }
//
//    @Override
//    public T visitVarable(SysYParser.VarableContext ctx) {
//        if (checkIsSameDecl(ctx.IDENT().getText())){
//            int lineNo = ctx.IDENT().getSymbol().getLine();
//            printError(lineNo,3);
//        }else {
//            SysYParser.VarDeclContext parentctx = (SysYParser.VarDeclContext)ctx.getParent();
//            Type type = null;
//            if (parentctx.bType().getText().equals("int")){
//                type = new BaseType();
//            }
//            String name = ctx.IDENT().getText();
//            IntSymbol symbol = new IntSymbol(name,type);
//            nowScope.define(symbol);
//        }
//
//        return super.visitVarable(ctx);
//    }
//
//    @Override
//    public T visitAssignedVarable(SysYParser.AssignedVarableContext ctx) {
//        if (checkIsSameDecl(ctx.IDENT().getText())){
//            int lineNo = ctx.IDENT().getSymbol().getLine();
//            printError(lineNo,3);
//        }else {
//            SysYParser.VarDeclContext parentctx = (SysYParser.VarDeclContext)ctx.getParent();
//            Type type = null;
//            if (parentctx.bType().getText().equals("int")){
//                type = new BaseType();
//            }
//            String name = ctx.IDENT().getText();
//            IntSymbol symbol = new IntSymbol(name,type);
//            nowScope.define(symbol);
//        }
//
//        return super.visitAssignedVarable(ctx);
//    }
//
//
//    @Override
//    public T visitFuncDef(SysYParser.FuncDefContext ctx) {
//        if (checkRedefineFunction(ctx.IDENT().getText())){
//            int lineNo = ctx.IDENT().getSymbol().getLine();
//            printError(lineNo,4);
//        } else {
//            String name = ctx.IDENT().getText();
//            Type retType = null;
//            //得到返回类型
//            if (ctx.funcType().getText().equals("int")){
//                retType = new BaseType();
//            }
//            //得到参数类型
//            ArrayList<Type> ptypes = new ArrayList<>();
//            if (ctx.funcFParams()!=null){
//                for (int i = 0;i<ctx.funcFParams().funcFParam().size();i++){
//                    String p = ctx.funcFParams().funcFParam(i).getText();
//                    if (p.contains("[")){
//                        ptypes.add(new ArrayType(new ArrayList<>(),1,new BaseType()));
//                    }else {
//                        ptypes.add(new BaseType());
//                    }
//                }
//            }
//
//            FunctionType functionType = new FunctionType(retType,ptypes);
//            Symbol Symbol = new Symbol(name,nowScope,functionType);
//            nowScope.define(Symbol);
//            nowScope.addChildScope(Symbol);
//            nowScope = Symbol;
//            nowisFunction = 1;
//        }
//
//        return super.visitFuncDef(ctx);
//    }
//
//    @Override
//    public T visitFuncFParam(SysYParser.FuncFParamContext ctx) {
//        if (checkIsSameDecl(ctx.IDENT().getText())){
//            int lineNo = ctx.IDENT().getSymbol().getLine();
//            printError(lineNo,3);
//        }else {
//            Type type = null;
//            if (ctx.bType().getText() == "int"&&ctx.getText().contains("[")){
//                type = new ArrayType(new ArrayList<>(),1,new BaseType());
//            }else {
//                type = new BaseType();
//            }
//            String name = ctx.IDENT().getText();
//            IntSymbol symbol = new IntSymbol(name,type);
//            nowScope.define(symbol);
//        }
//
//        return super.visitFuncFParam(ctx);
//    }
//
//    @Override
//    public T visitTerminal(TerminalNode node) {
//        String content = node.getText();
//        if (content.equals("}")){
//            RuleNode parent = (RuleNode) node.getParent();
//            //如果是block的右大括号
//            if (parent.getRuleContext().getRuleIndex()==14) {
//                if (nowScope.fatherScope!=null){
//                    nowScope = nowScope.fatherScope;
//                }
//
//            }
//        }
//        return super.visitTerminal(node);
//    }
//
//        @Override
//    public T visitBlock(SysYParser.BlockContext ctx) {
//        if (nowisFunction == 0){
//            LocalScope localScope = new LocalScope(nowScope);
//            nowScope.addChildScope(localScope);
//            nowScope = localScope;
//        }
//        if (nowisFunction == 1){
//            nowisFunction = 0;
//        }
//        return super.visitBlock(ctx);
//    }
//
//
//    @Override
//    public T visitFunctionCall(SysYParser.FunctionCallContext ctx) {
//        ArrayList<String> params = new ArrayList<>();
//        //这一部分要处理几个错误
//        //首先检查是否对变量进行了函数调用
//        if (checkIsCallVara(ctx.IDENT().getText())){
//            int lineNo = ctx.IDENT().getSymbol().getLine();
//            printError(lineNo,10);
//        }
//        //接下来是调用未定义函数
//        else if (checkIsUndefFunc(ctx.IDENT().getText())){
//            int lineNo = ctx.IDENT().getSymbol().getLine();
//            printError(lineNo,2);
//        }else {
//            funcForCall = (Symbol) scope.resolve(ctx.IDENT().getText());
//        }
//
//        return super.visitFunctionCall(ctx);
//    }
//
//    @Override
//    public T visitFuncRParams(SysYParser.FuncRParamsContext ctx) {
//        if (funcForCall!=null){
//            ArrayList<String> params = new ArrayList<>();
//            //处理函数参数调用错误的问题
//            for (int i = 0;i<ctx.param().size();i++){
//                String theParam = ctx.param(i).getText();
//                params.add(ctx.param(i).getText());
//            }
//            FunctionType fType = (FunctionType) funcForCall.type;
//                if (fType.paramsType.size()!= params.size()){
//                    int lineNo = ctx.start.getLine();
//                    printError(lineNo,8);
//                }
//                else if (checkWrongParam(params,fType)){
//                    int lineNo = ctx.start.getLine();
//                    printError(lineNo,8);
//                }
//                funcForCall = null;
//        }
//
//        return super.visitFuncRParams(ctx);
//    }
//
//    //对应错误2，检查调用了未定义函数
//    boolean checkIsUndefFunc(String name){
//        //检查全局作用域
//        int size=scope.symbols.size();
//        if (scope.symbols.get(name) == null){
//            return true;
//        }
//        return false;
//
//    }
//
//    //对应错误3，检查重复定义变量问题
//    boolean checkIsSameDecl(String name){
//        int size=nowScope.symbols.size();
//        for (int i = 0;i<size;i++){
//            if (nowScope.symbols.get(name)!=null){
//                Jishu.haserror = 1;
//                return true;
//            }
//        }
//        return false;
//
//    }
//
//    //对应错误4，检查重定义函数
//    boolean checkRedefineFunction(String name){
//        int size=nowScope.symbols.size();
//            if (scope.symbols.get(name) != null){
//                Jishu.haserror = 1;
//                return true;
//            }
//        return false;
//    }
//
//    //对应错误8，检查函数参数适用
//    boolean checkWrongParam(ArrayList<String> params,FunctionType functionType){
//        for (int i = 0;i<params.size();i++){
//            String thep = params.get(i);
//            Type thistype = null;
//            //变量
//            if (!thep.contains("(")&&(thep.charAt(0) == '_'||(thep.charAt(0) >= 'a'&&thep.charAt(0) <= 'z')||(thep.charAt(0) >= 'A'&&thep.charAt(0) <= 'Z'))){
//                Symbol symbol = nowScope.resolve(thep);
//                thistype = symbol.getType();
//                System.out.println(thep);
//            }else if ((thep.charAt(0) == '_'||(thep.charAt(0) >= 'a'&&thep.charAt(0) <= 'z')||(thep.charAt(0) >= 'A'&&thep.charAt(0) <= 'Z'))&&thep.contains("(")){
//                int dex = thep.indexOf('(');
//
//            }
//        }
//        return false;
//    }
//
//    //对应错误10，对变量进行函数调用
//    boolean checkIsCallVara(String name){
//        Scope sss = nowScope;
//        while (sss!= null){
//            if (sss.symbols.get(name)!= null){
//                if (sss.symbols.get(name).getType().value == 1 || sss.symbols.get(name).getType().value == 3){
//                    return true;
//                }
//            }
//            sss = sss.fatherScope;
//        }
//        return false;
//    }
//
//    //工具函数：从当前作用域开始查找symbol
//
//    void printError(int LineNO,int errorType){
//        System.err.println("Error type "+errorType+" at Line "+LineNO+":");
//    }
//
//
//}
