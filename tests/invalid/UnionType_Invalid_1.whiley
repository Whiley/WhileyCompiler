import * from whiley.lang.*

type IntReal is int | real

function f(int x) -> int:
    return x

method main(System.Console sys) -> void:
    int x
    //
    if |sys.args| > 0:
        x = 1.23
    else:
        x = 1
    sys.out.println(Any.toString(x))
    f(x)
