import * from whiley.lang.*

function f(int x) -> int:
    if x < 10:
        return 1
    else:
        if x > 10:
            return 2

method main(System.Console sys) -> void:
    sys.out.println(Any.toString(f(1)))
    sys.out.println(Any.toString(f(10)))
    sys.out.println(Any.toString(f(11)))
    sys.out.println(Any.toString(f(1212)))
    sys.out.println(Any.toString(f(-1212)))
