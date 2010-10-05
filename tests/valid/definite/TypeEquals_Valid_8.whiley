define pos as real where $ > 0
define neg as int where $ < 0
define expr as pos|neg

void f(expr e):
    if e ~= pos:
        out->println("POSITIVE): " + str(e)
    else:
        out->println("NEGATIVE): " + str(e)

void System::main([string] args):
    f(-1)
    f(1)
    f(1234)
 
