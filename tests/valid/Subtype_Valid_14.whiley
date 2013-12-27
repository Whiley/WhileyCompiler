import println from whiley.lang.System

define sr7nat as int where $ > 0

void ::main(System.Console sys):
    x = {f:1}
    x.f = x.f + 1
    sys.out.println(Any.toString(x))
    
