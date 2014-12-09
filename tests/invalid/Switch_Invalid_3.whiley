import * from whiley.lang.*

function f(int x) -> int:
    switch x:
        default:
            return 0
        default:
            return 1
    return 10

method main(System.Console sys) -> void:
    sys.out.println(Any.toString(f(1)))
    sys.out.println(Any.toString(f(2)))
    sys.out.println(Any.toString(f(3)))
    sys.out.println(Any.toString(f(-1)))
