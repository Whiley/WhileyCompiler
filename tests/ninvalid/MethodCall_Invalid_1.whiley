import * from whiley.lang.*

function f(int x) => void:
    sys.out.println(Any.toString(x))

method main(System.Console sys) => void:
    this.f(1)
