import println from whiley.lang.System

void ::main(System.Console sys):
    x = 1234567891011121314151617181920
    x = x + 1
    sys.out.println(Any.toString(x))
