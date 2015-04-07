import whiley.lang.*

function f(int x) -> {int=>int}:
    return {1=>x, 3=>2}

function get(int i, {int=>int} map) -> int:
    return map[i]

method main(System.Console sys) -> void:
    sys.out.println(get(1, f(1)))
    sys.out.println(get(1, f(2)))
    sys.out.println(get(1, f(3)))
    sys.out.println(get(3, f(3)))
