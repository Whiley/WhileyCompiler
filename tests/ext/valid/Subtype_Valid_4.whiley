import * from whiley.lang.*

define sr4set as {int} where |$| > 0

void ::main(System.Console sys,[string] args):
    x = {1}
    sys.out.println(Any.toString(x))
    
