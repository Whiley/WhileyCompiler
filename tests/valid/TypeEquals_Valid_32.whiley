import whiley.lang.System

type expr is {int} | bool

function g({int} input) => {int}:
    return input + {-1}

function f(expr e) => string:
    if e is {int}:
        {int} t = g(e)
        return "GOT: " ++ Any.toString(t)
    else:
        return "GOT SOMETHING ELSE?"

method main(System.Console sys) => void:
    {int} e = {1, 2, 3, 4}
    sys.out.println(f(e))
