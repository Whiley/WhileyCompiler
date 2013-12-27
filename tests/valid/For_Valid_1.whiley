import println from whiley.lang.System

void ::main(System.Console sys):
    xs = [1,2,3]
    for st in xs:
        sys.out.println(Any.toString(st))
