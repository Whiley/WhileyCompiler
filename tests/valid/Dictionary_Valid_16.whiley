import whiley.lang.*

function f(int x) -> {int=>int}:
    return {1=>x, 3=>2}

method main(System.Console sys) -> void:
    sys.out.println(f(1))
    sys.out.println(f(2))
    sys.out.println(f(3))
