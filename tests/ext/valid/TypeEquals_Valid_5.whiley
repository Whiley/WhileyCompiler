import whiley.lang.*:*

define pos as int where $ > 0
define neg as int where $ < 0
define expr as pos|neg

string f(expr e):
    if e is pos:
        return "POSITIVE: " + str(e)
    else:
        return "NEGATIVE: " + str(e)

void ::main(System sys,[string] args):
    sys.out.println(f(-1))
    sys.out.println(f(1))
    sys.out.println(f(1234))
 
