import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import java.io.IOException;
import java.util.List;

public class Main
{
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("input path is required");
        }
        String source = args[0];
        CharStream input = CharStreams.fromFileName(source);
        SysYLexer sysYLexer = new SysYLexer(input);
        sysYLexer.removeErrorListeners();
        myErrorListener my_listener = new myErrorListener();
        sysYLexer.addErrorListener(my_listener);
        List<? extends Token> res=sysYLexer.getAllTokens();
        String[] BigList = new String[] {
                null, "CONST", "INT", "VOID", "IF", "ELSE", "WHILE", "BREAK", "CONTINUE",
                "RETURN", "PLUS", "MINUS", "MUL", "DIV", "MOD", "ASSIGN", "EQ", "NEQ",
                "LT", "GT", "LE", "GE", "NOT", "AND", "OR", "L_PAREN", "R_PAREN", "L_BRACE",
                "R_BRACE", "L_BRACKT", "R_BRACKT", "COMMA", "SEMICOLON", "IDENT", "INTEGR_CONST",
                "WS", "LINE_COMMENT", "MULTILINE_COMMENT"
        };

        if(my_listener.have_fault()==0){
            for (Token re : res) {
                if (re.getType() != 34) {
                    System.err.println(BigList[re.getType()] + " " + re.getText() + " at Line " + re.getLine() + ".");
                } else {
                    String NUMBER_STRING = re.getText();
                    if (NUMBER_STRING.length()==1||NUMBER_STRING.charAt(0)!='0') {
                        System.err.println(BigList[re.getType()] + " " + re.getText() + " at Line " + re.getLine() + ".");
                    } else if (NUMBER_STRING.charAt(0) == '0' && (NUMBER_STRING.charAt(1)!='x' && NUMBER_STRING.charAt(1) !='X')) {
                        int NUMBER = Integer.parseInt(NUMBER_STRING.substring(1),8);
                        System.err.println(BigList[re.getType()] + " " + NUMBER + " at Line " + re.getLine() + ".");
                    } else {
                        if(NUMBER_STRING.charAt(1) == 'X'){
                            NUMBER_STRING = NUMBER_STRING.charAt(0) + "x" + NUMBER_STRING.substring(2);
                        }
                        int NUMBER = Integer.parseInt(NUMBER_STRING.substring(2),16);
                        System.err.println(BigList[re.getType()] + " " + NUMBER + " at Line " + re.getLine() + ".");
                    }
                }
            }
        }
    }
}