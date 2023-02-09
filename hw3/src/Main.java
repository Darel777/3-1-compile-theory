import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.util.List;

public class Main
{
    public static int haserror = 0;
    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            System.err.println("lack input");
        }
       //"tests/newtest.sysy"
        String source =args[0];
        int lineNo = Integer.parseInt(args[1]);
        int charIndex = Integer.parseInt(args[2]);
        String rename = args[3];
//        String source ="tests/newtest.sysy";
//        int lineNo = 8;
//        int charIndex = 4;
//        String rename = "d";


        CharStream input = CharStreams.fromFileName(source);
        //sysYLexer构建
        SysYLexer sysYLexer = new SysYLexer(input);
        MyErrorListener myErrorListener = new MyErrorListener();
        sysYLexer.removeErrorListeners();
        sysYLexer.addErrorListener(myErrorListener);

        //sysYParser构建
        CommonTokenStream tokens = new CommonTokenStream(sysYLexer);
        SysYParser sysYParser = new SysYParser(tokens);
        sysYParser.removeErrorListeners();
        MyErrorListenerOfParser myErrorListenerOfParser = new MyErrorListenerOfParser();
        sysYParser.addErrorListener(myErrorListenerOfParser);

//        ParseTree tree = sysYParser.program();
//        Visitor visitor = new Visitor();
//        visitor.visit(tree);
//        Scope allscope = new GlobalScope(null);
        Scope allscope;
        ParseTree tree = sysYParser.program();
        MyVisitor visitor = new MyVisitor();
        visitor.visit(tree);
        allscope = visitor.globalScope;
        Scope nowscope = visitor.nowScope;
        if (Jishu.haserror == 0){
            RenameVisitor renameVisitor = new RenameVisitor(allscope,lineNo,charIndex,rename);
            renameVisitor.visit(tree);
            RenamePrintVisitor renamePrintVisitor = new RenamePrintVisitor(allscope,rename);
            renamePrintVisitor.visit(tree);
            System.out.println(1);
        }



    }
}

class MyErrorListener extends BaseErrorListener
{

    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        System.err.println("Error type A at Line " + line + ":" + ".");
        Jishu j = new Jishu();
        Jishu.haserror =1;

    }


}

class MyErrorListenerOfParser extends BaseErrorListener
{

    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            System.err.println("Error type B at Line " + line + ":" + ".");
        Jishu j = new Jishu();
        Jishu.haserror =1;

    }


}


