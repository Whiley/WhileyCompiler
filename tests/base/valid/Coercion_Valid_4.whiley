import * from whiley.lang.*

{int=>real} f([real] x):
    return x

void ::main(System sys,[string] args):
    sys.out.println(Any.toString(f([1.2,2.3])))