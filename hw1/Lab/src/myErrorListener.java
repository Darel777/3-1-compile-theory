import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
public class myErrorListener extends BaseErrorListener {
    private int err_argument = 0;
    public myErrorListener(){
    }
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        System.err.println("Error type A at Line "+line+": "+msg);
        this.err_argument+=1;
    }
    public int have_fault(){
        return this.err_argument;
    }
}
