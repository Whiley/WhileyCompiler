import whiley.lang.*

function f([int] xs, [real] ys) -> bool:
    if (([real]) xs) == ys:
        return true
    else:
        return false

method main(System.Console sys) -> void:
    sys.out.println(f([1, 4], [1.0, 4.0]))
    sys.out.println(f([1, 4], [1.0, 4.2]))
    sys.out.println(f([], []))
