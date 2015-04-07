import whiley.lang.*

type func is function(int) -> int

function g(int y) -> func:
    return &(int x -> x + y)

method main(System.Console sys) -> void:
    func f = g(3)
    sys.out.println(f(1))
    sys.out.println(f(2))
    sys.out.println(f(3))
    f = g(19)
    sys.out.println(f(1))
    sys.out.println(f(2))
    sys.out.println(f(3))
