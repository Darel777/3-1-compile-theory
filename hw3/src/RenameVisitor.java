import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import types.Type;

public class RenameVisitor<T> extends SysYParserBaseVisitor<T>{
    public Scope globalScope;

    public Scope nowScope;
    int lineNO;
    int charIndex;
    String rename;
    int localIndex = 0;

    int isForFuncCall = 0;
    public RenameVisitor(Scope globalScope,int lineNO,int charIndex, String rename){
        super();
        this.globalScope = globalScope;
        this.lineNO = lineNO;
        this.charIndex = charIndex;
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

    @Override
    public T visitTerminal(TerminalNode node) {
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
        int thisLineNo = node.getSymbol().getLine();
        int thisCharIndex = node.getSymbol().getCharPositionInLine();
        if (thisLineNo == lineNO && thisCharIndex == charIndex){
            Symbol symbol = nowScope.resolve(node.getText());
            symbol.name = symbol.name+"Rename:"+rename;
        }
        return super.visitTerminal(node);
    }
}
