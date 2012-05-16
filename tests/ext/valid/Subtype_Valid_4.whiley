import println from whiley.lang.System

define sr4set as {int} where |$| > 0

void ::main(System.Console sys):
    x = {1}
    sys.out.println(Any.toString(x))
    
