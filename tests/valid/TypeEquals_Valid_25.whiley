import println from whiley.lang.System

[int] f([[real|int]] e):
    if e is [[int]]:
        return e[0]
    else:
        return [1,2,3]

void ::main(System.Console sys):
    sys.out.println(Any.toString(f([[1,2,3,4,5,6,7]])))
    sys.out.println(Any.toString(f([[]])))
    sys.out.println(Any.toString(f([[1,2,2.01]])))
    sys.out.println(Any.toString(f([[1.23,2,2.01]])))
