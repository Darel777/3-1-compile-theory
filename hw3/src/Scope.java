
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Scope {
    public String name;
    public Scope fatherScope;
    public Map<String , Symbol> symbols = new LinkedHashMap<>();

    public ArrayList<Scope> childScopes = new ArrayList<>();


    public Scope(String name, Scope fatherScope) {
        this.name = name;
        this.fatherScope = fatherScope;
    }

    public void define(Symbol symbol){
        symbols.put(symbol.name,symbol);
    }

    public Symbol resolve(String name){
        Symbol symbol = symbols.get(name);
        if (symbol!= null){
            return symbol;
        }
        if (fatherScope!=null){
            return fatherScope.resolve(name);
        }

        return null;
    }

    public String getName() {
        return this.name;
    }

    public Map<String, Symbol> getSymbols() {
        return symbols;
    }

    public ArrayList<Scope> getChildScopes() {
        return childScopes;
    }


    public Scope getFatherScope() {
        return this.fatherScope;
    }

    public void addChildScope(Scope scope){
        this.childScopes.add(scope);
    }
}


