import * from whiley.lang.*

void ::main(System sys,[string] args):
    ls = [true,false,true]
    sys.out.println(Any.toString(ls))
    x = ls[0]
    sys.out.println(Any.toString(x))
    ls[0] = false
    sys.out.println(Any.toString(ls))
