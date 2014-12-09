import * from whiley.lang.*

function f(System.Console x, int y) -> [int]:
    return [1, 2, 3, get(x)]

method get(System.Console this) -> int:
    return 1

method main(System.Console sys) -> void:
    sys.out.println(Any.toString(f(sys, 1)))
