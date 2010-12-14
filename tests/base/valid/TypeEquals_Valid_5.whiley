define pos as int
define neg as int
define expr as pos|neg|[int]

string f(expr e):
    if e ~= pos && e > 0:
        return "POSITIVE: " + str(e)
    else:
        return "NEGATIVE: " + str(e)

void System::main([string] args):
    out->println(f(-1))
    out->println(f(1))
    out->println(f(1234))
 
