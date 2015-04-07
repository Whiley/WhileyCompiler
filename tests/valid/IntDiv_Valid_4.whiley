import whiley.lang.*

function f(int x) -> int:
    return x / 3

public method main(System.Console sys) -> void:
    sys.out.println(f(10))
