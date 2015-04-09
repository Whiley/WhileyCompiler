import whiley.lang.*

type MyObject is &{System.Console sys}

method f(MyObject this, int x) -> void:
    assume x == 1

method main(System.Console sys) -> void:
    MyObject m = new {sys: sys}
    f(m,1)
    