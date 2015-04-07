import whiley.lang.*

type IntReal is int | real

function f(IntReal y) -> IntReal:
    return y

method main(System.Console sys) -> void:
    IntReal x = 123
    sys.out.println(f(x))
    x = 1.234
    sys.out.println(f(x))
