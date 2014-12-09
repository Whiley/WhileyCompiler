import * from whiley.lang.*

type IntReal is int | real

function f(int y) -> void:
    debug Any.toString(y)

method main(System.Console sys) -> void:
    int x
    //
    x = 123
    f(x)
    x = 1.234
    f(x)
