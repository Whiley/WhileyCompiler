import * from whiley.lang.*

method f(System.Console sys, int x) -> void:
    sys.out.println(Any.toString(x))

method main(System.Console sys) -> void:
    f(1)
