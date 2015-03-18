import whiley.lang.System

function f([int] xs, [real] ys) -> ASCII.string:
    if (([real]) xs) == ys:
        return "EQUAL"
    else:
        return "NOT EQUAL"

method g(System.Console sys, [int] xs, [real] ys) -> void:
    sys.out.println(xs)
    sys.out.println(ys)
    sys.out.println_s(f(xs, ys))

method main(System.Console sys) -> void:
    g(sys, [1, 4], [1.0, 4.0])
    g(sys, [1, 4], [1.0, 4.2])
    g(sys, [], [])
