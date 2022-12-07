import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import java.io.IOException;
import java.util.List;

public class Main
{
    public static void main(String[] args) throws IOException {
        //no path
        if (args.length < 1) {
            System.err.println("input path is required");
        }
        //get input
        String source = args[0];
        CharStream input = CharStreams.fromFileName(source);
        //sy sy lexer
        SysYLexer sysYLexer = new SysYLexer(input);
        sysYLexer.removeErrorListeners();
        MyErrorListener myErrorListener = new MyErrorListener();
        sysYLexer.addErrorListener(myErrorListener);
        //sy sy parser
        CommonTokenStream token = new CommonTokenStream(sysYLexer);
        SysYParser sysYParser = new SysYParser(token);
        sysYParser.removeErrorListeners();
        MyParserErrorListener myParserErrorListener = new MyParserErrorListener();
        sysYParser.addErrorListener(myParserErrorListener);
        //tree
        ParseTree tree = sysYParser.program();
        Visitor visitor = new Visitor();
        visitor.visit(tree);
    }
}