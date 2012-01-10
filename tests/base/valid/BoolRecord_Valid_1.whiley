import * from whiley.lang.*

void ::main(System.Console sys,[string] args):
    x = {flag:true,code:0}
    sys.out.println(Any.toString(x))
    x.flag = false
    sys.out.println(Any.toString(x))
