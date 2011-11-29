import * from whiley.lang.*

void ::main(System sys,[string] args):
    x = {flag:true,code:0}
    sys.out.println(toString(x))
    x.flag = false
    sys.out.println(toString(x))
