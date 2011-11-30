import * from whiley.lang.*

define a_nat as int
define b_nat as int

b_nat f(a_nat x):
    if x == 0:
        return 1
    else:
        return f(x-1)

void ::main(System sys,[string] args):
    x = |args|    
    x = f(x)    
    sys.out.println(Any.toString(x))
