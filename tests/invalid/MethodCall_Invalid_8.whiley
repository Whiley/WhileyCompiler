import * from whiley.lang.*

function f() => int:
    x = new 1
    return 1

method main(System.Console sys) => void:
    x = f()
    sys.out.println(Any.toString(x))
