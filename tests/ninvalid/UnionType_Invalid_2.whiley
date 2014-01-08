import * from whiley.lang.*

type IntReal is int | real

function f(int y) => void:
    sys.out.println(Any.toString(y))

method main(System.Console sys) => void:
    x = 123
    f(x)
    x = 1.234
    f(x)
