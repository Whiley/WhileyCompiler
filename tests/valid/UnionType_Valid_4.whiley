import whiley.lang.*

type IntReal is int | real

function f(IntReal y) -> IntReal:
    return y

method main(System.Console sys) -> void:
    IntReal x = 123
    assume f(x) == 123
    x = 1.234
    assume f(x) == 1.234
