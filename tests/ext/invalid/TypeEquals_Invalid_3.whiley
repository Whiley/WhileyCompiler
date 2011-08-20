import whiley.lang.*:*

define pos as int where $ > 0
define neg as int where $ < 0
define expr as pos|neg

void g(neg x):
    debug "NEGATIVE: " + str(x)

void f(expr e):
    if e is pos:
        g(e)

void System::main([string] args):
    f(-1)
    f(1)
 
