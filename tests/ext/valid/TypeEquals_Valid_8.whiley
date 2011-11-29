import * from whiley.lang.*

define pos as real where $ > 0
define neg as int where $ < 0
define expr as pos|neg

string f(expr e):
    if e is pos:
        return "POSITIVE: " + toString(e)
    else:
        return "NEGATIVE: " + toString(e)

void ::main(System sys,[string] args):
    sys.out.println(f(-1))
    sys.out.println(f(1))
    sys.out.println(f(1234))
 
