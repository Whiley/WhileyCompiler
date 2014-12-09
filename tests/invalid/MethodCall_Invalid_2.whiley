import * from whiley.lang.*

type dummy is &{int x}

method f(dummy this, int x) -> void:
    debug Any.toString(x)

method main(System.Console sys) -> void:
    f(sys, 1)
