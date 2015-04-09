import whiley.lang.*

type func is function(int) -> int

function g() -> func:
    return &(int x -> x + 1)

method main(System.Console sys) -> void:
    func f = g()
    assume f(1) == 2
    assume f(2) == 3
    assume f(3) == 4
