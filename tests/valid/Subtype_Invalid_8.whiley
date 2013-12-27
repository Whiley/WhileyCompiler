import println from whiley.lang.System

define sr6nat as int
define sr6tup as {sr6nat f, int g}

void ::main(System.Console sys):
    x = {f:1,g:5}
    x.f = 2
    sys.out.println(Any.toString(x))
    
