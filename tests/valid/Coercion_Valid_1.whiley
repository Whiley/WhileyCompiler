import whiley.lang.*

function f(int x) -> real:
    return (real) x

method main(System.Console sys) -> void:
    assume f(123) == 123.0
