import whiley.lang.*

type iset is {int} | int

function f(iset e) -> bool:
    if e is {int}:
        return true
    else:
        return false

method main(System.Console sys) -> void:
    sys.out.println(f({1, 2, 3}))
    sys.out.println(f(1))
