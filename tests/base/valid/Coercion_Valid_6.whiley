import * from whiley.lang.*

{real} f([real] x):
    return x

void ::main(System.Console sys):
    x = f([2.2,3.3])
    sys.out.println(Any.toString(x))