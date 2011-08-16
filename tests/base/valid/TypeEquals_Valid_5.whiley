define pos as int
define neg as int
define expr as pos|neg|[int]

string f(expr e):
    if e is pos && e > 0:
        return "POSITIVE: " + str(e)
    else:
        return "NEGATIVE: " + str(e)

void System::main([string] args):
    this.out.println(f(-1))
    this.out.println(f(1))
    this.out.println(f(1234))
 
