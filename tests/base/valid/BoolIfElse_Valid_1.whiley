import * from whiley.lang.*

string f(bool b):
    if(b):
        return "TRUE"
    else:
        return "FALSE"

void ::main(System sys,[string] args):
    sys.out.println(f(true))
    sys.out.println(f(false))
