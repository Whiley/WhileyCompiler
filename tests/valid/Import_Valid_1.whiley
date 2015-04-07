import whiley.lang.*

function f(int x) -> int:
    if x < 0:
        return 0
    else:
        return x

public method main(System.Console sys) -> void:
    sys.out.println(f(1))
