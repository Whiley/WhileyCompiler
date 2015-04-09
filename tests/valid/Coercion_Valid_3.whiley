import whiley.lang.*

function f(int x) -> int:
    return (int) x

method main(System.Console sys) -> void:
    assume f('H') == 72
