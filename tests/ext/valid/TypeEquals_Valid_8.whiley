import whiley.lang.*:*

define pos as real where $ > 0
define neg as int where $ < 0
define expr as pos|neg

string f(expr e):
    if e is pos:
        return "POSITIVE: " + str(e)
    else:
        return "NEGATIVE: " + str(e)

void System::main([string] args):
    this.out.println(f(-1))
    this.out.println(f(1))
    this.out.println(f(1234))
 
