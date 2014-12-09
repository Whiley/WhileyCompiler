import * from whiley.lang.*

function f(System.Console x, int y) -> int:
    return x.get()

method get(System this) -> int:
    return 1

method main(System.Console sys) -> void:
    sys.out.println(Any.toString(f(this, 1)))
