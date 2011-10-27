import * from whiley.lang.*

void ::main(System sys,[string] args):
    ls = [true,false,true]
    sys.out.println(toString(ls))
    x = ls[0]
    sys.out.println(toString(x))
    ls[0] = false
    sys.out.println(toString(ls))
