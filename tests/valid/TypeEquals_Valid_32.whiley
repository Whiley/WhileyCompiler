import whiley.lang.*

type expr is {int} | bool

function g({int} input) -> {int}:
    return input + {-1}

function f(expr e) -> {int}:
    if e is {int}:
        {int} t = g(e)
        return t
    else:
        return {}

method main(System.Console sys) -> void:
    {int} e = {1, 2, 3, 4}
    sys.out.println(f(e))
    sys.out.println(f(false))
