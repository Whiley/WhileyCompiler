import whiley.lang.*:*

define sr7nat as int

void ::main(System sys,[string] args):
    x = {f:1}
    x.f = x.f + 1
    sys.out.println(str(x))
    
