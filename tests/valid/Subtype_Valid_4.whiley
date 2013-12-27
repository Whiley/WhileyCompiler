import println from whiley.lang.System

define sr8nat as int
define sr8tup as {sr8nat f, int g}

void ::main(System.Console sys):
    x = [{f:1,g:3},{f:4,g:8}]
    x[0].f = 2
    sys.out.println(Any.toString(x))
    
