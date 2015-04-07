import whiley.lang.*

function f(int x) -> real:
    return (real) x

method main(System.Console sys) -> void:
    sys.out.println(f(123))
