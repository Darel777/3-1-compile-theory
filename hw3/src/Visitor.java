import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class Visitor<T> extends SysYParserBaseVisitor<T>{
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
//                if (content.equals("0")){
//
//                }else if (content.charAt(0) != '0'){
//
//                }else if (content.charAt(0) == '0' && content.charAt(1) >= '0' && content.charAt(1)<='7'  ){
//                        text = Integer.parseInt(content,8);content = String.valueOf(text);
//                    } else if (content.charAt(0) == '0' && (content.charAt(1) == 'x'|| content.charAt(1) == 'X')){
//                        text = Integer.parseInt(content.substring(2),16);
//                        content = String.valueOf(text);
//                    }
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
            printed+=content+" "+ rules + "[" + color + "]" ;
            if (!color.equals("")){
                System.err.println(printed);
            }
        }



        return result;
    }

}
