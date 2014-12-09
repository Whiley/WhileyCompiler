import * from whiley.lang.*

function f(int x) -> void:
    debug Any.toString(x)

method main(System.Console sys) -> void:
    f({})
