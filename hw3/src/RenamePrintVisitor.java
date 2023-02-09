import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class RenamePrintVisitor<T> extends SysYParserBaseVisitor<T>{
    Scope globalScope;
    String rename;
    public Scope nowScope;

    int localIndex = 0;

    int isForFuncCall = 0;
    public RenamePrintVisitor(Scope globalScope, String rename) {
        this.globalScope = globalScope;
        this.rename = rename;
    }


    @Override
    public T visitProgram(SysYParser.ProgramContext ctx) {
        nowScope = globalScope;
        return super.visitProgram(ctx);
    }

    @Override
    public T visitFuncDef(SysYParser.FuncDefContext ctx) {
        String funcName = ctx.IDENT().getText();
        for (int i = 0;i<globalScope.childScopes.size();i++){
            if (funcName.equals(globalScope.childScopes.get(i).name)){
                nowScope = globalScope.childScopes.get(i);
                isForFuncCall = 1;
                break;
            }
        }
        return super.visitFuncDef(ctx);
    }


    @Override
    public T visitBlock(SysYParser.BlockContext ctx) {
        if (isForFuncCall == 0){
            String name = "LocalScope"+localIndex;
            localIndex++;
            for (int i = 0;i<nowScope.childScopes.size();i++){
                if (name.equals(nowScope.childScopes.get(i).name)){
                    nowScope = nowScope.childScopes.get(i);
                    break;
                }
            }
        }
        if (isForFuncCall == 1){
            isForFuncCall = 0;
        }
        return super.visitBlock(ctx);
    }

    public T visitChildren(RuleNode node) {
        T result = null;
        int n = node.getChildCount();
        int index = node.getRuleContext().getRuleIndex();
        int d = node.getRuleContext().depth();
        String name = SysYParser.ruleNames[index];
        String upname = name.toUpperCase();
        String printed = "";
        for (int i = 0;i<d-1;i++){
            printed += "  ";
        }
        printed += upname.charAt(0) + name.substring(1);
        if (Jishu.haserror == 0){
            System.err.println(printed);
        }

        for(int i = 0; i < n && this.shouldVisitNextChild(node, result); ++i) {
            ParseTree c = node.getChild(i);
            T childResult = c.accept(this);
            result = this.aggregateResult(result, childResult);
        }
        return result;
    }

    public T visitTerminal(TerminalNode node) {
        T result = null;
        String content = node.getText();
        if (content.equals("}")){
            RuleNode parent = (RuleNode) node.getParent();
            //如果是block的右大括号
            if (parent.getRuleContext().getRuleIndex()==14) {
                if (nowScope.fatherScope!=null){
                    nowScope = nowScope.fatherScope;
                }

            }
        }
        int type = node.getSymbol().getType();
        if (type >=1 && Jishu.haserror == 0){
            String rules = SysYLexer.ruleNames[type-1];
            String color = "";
            if (type>= 1 && type <= 9){
                color = "orange";
            }else if (type >= 10 && type <= 24){
                color = "blue";
            }else if (type == 33){
                color = "red";
            }else if(type == 34){
                color = "green";
                int text = 0;
                if (content.length()>2){
                    if (content.charAt(0) == '0' && (content.charAt(1) == 'x'|| content.charAt(1) == 'X')){
                        text = Integer.parseInt(content.substring(2),16);
                        content = String.valueOf(text);
                    } else if (content.charAt(0) == '0' && content.charAt(1) >= '0' && content.charAt(1)<='7' ){
                        text = Integer.parseInt(content,8);content = String.valueOf(text);
                    }
                }
            }
            String printed = "";
            RuleNode parent = (RuleNode) node.getParent();
            for (int i = 0;i<parent.getRuleContext().depth();i++){
                printed+="  ";
            }
            //重命名
            if (node.getSymbol().getType() == SysYLexer.IDENT){
                Symbol symbol= nowScope.resolve(content);
                if (symbol.name.contains("Rename:")){
                    content = rename;
                }
            }

            printed+=content+" "+ rules + "[" + color + "]" ;
            if (!color.equals("")){
                System.err.println(printed);
            }
        }

        return result;
    }
}
