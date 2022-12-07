import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.tool.Rule;

public class Visitor<T> extends SysYParserBaseVisitor<T>{
    public T visitChildren(RuleNode node) {
        T result = null;
        int n = node.getChildCount();
        int index = node.getRuleContext().getRuleIndex();
        int dep = node.getRuleContext().depth();
        String name = SysYParser.ruleNames[index];
        String upper_name = name.toUpperCase();
        StringBuilder printed = new StringBuilder();
        printed.append("  ".repeat(Math.max(0, dep - 1)));
        printed.append(upper_name.charAt(0)).append(name.substring(1));
        if (MyParserErrorListener.err_argument == 0){
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
        String content = node.getText();
        int type = node.getSymbol().getType();
        if (type >=1 && MyParserErrorListener.err_argument == 0){
            String rules = SysYLexer.ruleNames[type-1];
            String color = "";
            if (type <= 9){
                color = "orange";
            }else if (type <= 24){
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
            StringBuilder printed = new StringBuilder();
            RuleNode parent = (RuleNode) node.getParent();
            printed.append("  ".repeat(Math.max(0, parent.getRuleContext().depth())));
            printed.append(content).append(" ").append(rules).append("[").append(color).append("]");
            if (!color.equals("")){
                System.err.println(printed);
            }
        }
        return null;
    }
}
