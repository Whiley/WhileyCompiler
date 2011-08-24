import whiley.lang.*:*

void ::main(System sys,[string] args):
    x = {flag:true,code:0}
    if(x.flag):
        sys.out.println("GOT HERE")
