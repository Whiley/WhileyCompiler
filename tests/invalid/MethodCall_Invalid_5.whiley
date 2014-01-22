import * from whiley.lang.*

function f(System x, int y) => [int]:
    return [1, 2, 3, x.get()]

method get(System this) => int:
    return 1

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f(this, 1)))
