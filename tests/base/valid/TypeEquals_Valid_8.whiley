import whiley.lang.*:*

define pos as real
define neg as int
define expr as pos|neg|[int]

string f(expr e):
    if e is pos && e > 0:
        return "POSITIVE: " + str(e)
    else if e is neg:
        return "NEGATIVE: " + str(e)
    else:
        return "OTHER"

void System::main([string] args):
    this.out.println(f(-1))
    this.out.println(f(1.0))
    this.out.println(f(1.234))
    this.out.println(f([1,2,3]))
 
