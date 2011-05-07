// expression tree
define Expr as real |  // constant
    [Expr] |           // list constructor
    ListAccess         // list access

// list access
define ListAccess as { 
    Expr src, 
    Expr index
} 

define Value as real | [Value] | null

Value evaluate(Expr e):
    if e ~= int:
        return e
    else if e ~= [Expr]:
        r = []
        for i in e:
            r = r + [evaluate(i)]
        return r
    else if e ~= ListAccess:
        src = evaluate(e.src)
        index = evaluate(e.index)
        // santity checks
        if src ~= [Expr] && index ~= int && index >= 0 && index < |src|:
            return src[index]
        else:
            return null // stuck
    else:
        // e must be a list expression
        return 0

public void System::main([string] args):
    out<->println(str(evaluate(1)))
    out<->println(str(evaluate({src: [112,212,342], index:0})))
    out<->println(str(evaluate({src: [112312,289712,31231242], index:2})))
    out<->println(str(evaluate([1,2,3])))
