import * from whiley.lang.*

define pos as int where $ > 0
define neg as int where $ < 0
define expr as pos|neg

void g(neg x):
    debug "NEGATIVE: " + toString(x)

void f(expr e):
    if e is pos:
        g(e)

void ::main(System sys,[string] args):
    f(-1)
    f(1)
 
