import whiley.lang.*

function f(int x) -> int:
    return (int) x

method main(System.Console sys) -> void:
    sys.out.println(f('H'))
