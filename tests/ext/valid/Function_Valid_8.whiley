import * from whiley.lang.*

define fr8nat as int where $ > 0
define fr8neg as int where $ < 0

string f(fr8nat y):
    return "F(NAT)"

string f(fr8neg x):
    return "F(NEG)"

void ::main(System sys,[string] args):
    sys.out.println(f(-1))
    sys.out.println(f(1))
