import println from whiley.lang.System

define a_nat as int where $ >= 0
define b_nat as int where 2*$ >= $

b_nat f(a_nat x):
    if x == 0:
        return 1
    else:
        return f(x-1)

void ::main(System.Console sys):
    x = |sys.args|    
    x = f(x)    
    sys.out.println(Any.toString(x))
