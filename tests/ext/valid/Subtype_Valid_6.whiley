import whiley.lang.*:*

define sr6nat as int where $ > 0
define sr6tup as {sr6nat f, int g} where g > f

void ::main(System sys,[string] args):
    x = {f:1,g:5}
    x.f = 2
    sys.out.println(str(x))
    
