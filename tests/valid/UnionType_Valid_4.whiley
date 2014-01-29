import whiley.lang.System

type IntReal is int | real

function f(IntReal y) => string:
    return Any.toString(y)

method main(System.Console sys) => void:
    IntReal x = 123
    sys.out.println(f(x))
    x = 1.234
    sys.out.println(f(x))
