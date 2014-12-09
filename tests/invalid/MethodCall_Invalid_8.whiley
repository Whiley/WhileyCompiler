import * from whiley.lang.*

function f() -> int:
    &int x = new 1
    return 1

method main(System.Console sys) -> void:
    int x = f()
    sys.out.println(Any.toString(x))
