import whiley.lang.*:*

define expr as [int]|int

string f(expr e):
    if e is [int]:
        return "GOT [INT]"
    else:
        return "GOT INT"

void ::main(System sys,[string] args):
    e = 1
    sys.out.println(f(e))
    e = [1,2,3,4]
    sys.out.println(f(e))
 
