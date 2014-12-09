import * from whiley.lang.*

function f(int x) -> int:
    sys.out.println(Any.toString(x))

method main(System.Console sys) -> void:
    f(1)
