import println from whiley.lang.System

define sr9nat as int
define sr9tup as {sr9nat f, int g}
define sr9arr as [{sr9nat f, int g}]

void ::main(System.Console sys):
    x = [{f:1,g:2},{f:1,g:8}]
    x[0].f = 2 
    sys.out.println(Any.toString(x))
