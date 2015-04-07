import whiley.lang.*

type func is function(int) -> int

function g() -> func:
    return &(int x -> x + 1)

method main(System.Console sys) -> void:
    func f = g()
    sys.out.println(f(1))
    sys.out.println(f(2))
    sys.out.println(f(3))
