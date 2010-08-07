define pos as int requires $ > 0
define neg as int requires $ < 0
define expr as pos|neg

void g(neg x):
    print "NEGATIVE: " + str(x)

void f(expr e):
    if e ~= pos:
        g(e)

void System::main([string] args):
    f(-1)
    f(1)
 
