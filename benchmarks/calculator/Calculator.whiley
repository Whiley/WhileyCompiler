// Main method
public void System::main([string] args):
    if(|args| > 0):
        e = parse(args[0])
        if e ~= {[int] err}:
            print "syntax error: " + e.err
        else:
            result = evaluate(e)
            print str(result)
    else:
        print "no parameter provided!"
