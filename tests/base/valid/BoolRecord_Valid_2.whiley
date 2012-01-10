import * from whiley.lang.*

void ::main(System.Console sys,[string] args):
    x = {flag:true,code:0}
    if(x.flag):
        sys.out.println("GOT HERE")
