parser grammar SysYParser;

options{
    tokenVocab=SysYLexer;
}

program
   : compUnit
   ;

compUnit
   : (funcDef | decl)+ EOF
   ;

decl:constDecl
| varDecl
    ;

constDecl:CONST bType constDef (COMMA constDef)* SEMICOLON
    ;

bType:INT
    ;

constDef:IDENT (L_BRACKT constExp R_BRACKT)+ ASSIGN constInitVal #constArray|
IDENT ASSIGN constInitVal #constVarable

    ;

constInitVal:constExp
    | L_BRACE (constInitVal (COMMA constInitVal)*)? R_BRACE
    ;

varDecl:bType varDef (COMMA varDef)* SEMICOLON
    ;

varDef:IDENT (L_BRACKT constExp R_BRACKT)+ #Array|
    IDENT #Varable| IDENT ASSIGN initVal #AssignedVarable
    | IDENT (L_BRACKT constExp R_BRACKT)+ ASSIGN initVal #AssignedArray
    ;

initVal:exp
    | L_BRACE (initVal (COMMA initVal)*)? R_BRACE
    ;

funcDef:funcType IDENT L_PAREN (funcFParams)? R_PAREN block
    ;

funcType:VOID | INT
    ;

funcFParams:funcFParam (COMMA funcFParam)*
    ;

funcFParam: bType IDENT (L_BRACKT  R_BRACKT(L_BRACKT exp R_BRACKT)*)?
    ;

block:L_BRACE (blockItem)* R_BRACE
    ;

blockItem: decl | stmt
    ;

stmt:lVal ASSIGN exp SEMICOLON #assign_stmt
    | (exp)? SEMICOLON #eeestmt
    | block #block_stmt
    | IF L_PAREN cond R_PAREN stmt (ELSE stmt)? #ifelse_stmt
    | WHILE L_PAREN cond R_PAREN stmt #while_stmt
    | BREAK SEMICOLON #break_stmt
    | CONTINUE SEMICOLON #continue_stmt
    | RETURN (exp)? SEMICOLON #return_stmt
    ;


exp
   : L_PAREN exp R_PAREN #EXPR
   | lVal #Lvalue
   | number #Shuzi
   | IDENT L_PAREN funcRParams? R_PAREN #FunctionCall
   | unaryOp exp #UNOP
   | exp (MUL | DIV | MOD) exp #MDMOP
   | exp (PLUS | MINUS) exp #PMOP
   ;

cond
   : exp #condExp
   | cond (LT | GT | LE | GE) cond #LGLG
   | cond (EQ | NEQ) cond #ENEQ
   | cond AND cond #condAND
   | cond OR cond #condOR
   ;

lVal
   : IDENT (L_BRACKT exp R_BRACKT)*
   ;

number
   : INTEGR_CONST
   ;

unaryOp
   : PLUS
   | MINUS
   | NOT
   ;

funcRParams
   : param (COMMA param)*
   ;

param
   : exp
   ;

constExp
   : exp
   ;