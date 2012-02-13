import * from whiley.lang.*

{(int,real)} f({int=>real} x):
    return x

void ::main(System.Console sys):
    x = f({1=>2.2,2=>3.3})
    sys.out.println(Any.toString(x))