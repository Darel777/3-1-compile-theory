import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class MyParserErrorListener extends BaseErrorListener {
    public static int err_argument=0;
    public MyParserErrorListener(){
    }
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        System.err.println("Error type B at Line "+line+": "+msg);
        err_argument+=1;
    }
    public int have_fault(){
        return err_argument;
    }

}
