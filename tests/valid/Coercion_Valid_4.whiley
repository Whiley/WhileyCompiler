import println from whiley.lang.System

{int=>real} f([real] x):
    return x

void ::main(System.Console sys):
    sys.out.println(Any.toString(f([1.2,2.3])))
