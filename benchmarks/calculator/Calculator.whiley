// Main method
public void System::main([string] args):
    if(|args| > 0):
        e = parse(args[0])
        if e ~= SyntaxError:
            print "syntax error: " + e.err
        else if e ~= Expr:
            result = evaluate(e)
            print str(result)
    else:
        print "no parameter provided!"
