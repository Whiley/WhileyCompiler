import whiley.lang.System

type iset is {int} | int

function f(iset e) => string:
    if e is {int}:
        return "{int}"
    else:
        return "int"

method main(System.Console sys) => void:
    sys.out.println(f({1, 2, 3}))
    sys.out.println(f(1))
