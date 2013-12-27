import println from whiley.lang.System

void ::main(System.Console sys):
    l = [1,2,3]
    r = [4.23,5.5]
    r = r + l
    sys.out.println(Any.toString(r))
