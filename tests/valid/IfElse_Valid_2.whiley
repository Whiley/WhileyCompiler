import whiley.lang.System

function f(int x) -> int:
    if x < 10:
        return 1
    else:
        if x > 10:
            return 2
    return 0

method main(System.Console sys) -> void:
    sys.out.println(f(1))
    sys.out.println(f(10))
    sys.out.println(f(11))
    sys.out.println(f(1212))
    sys.out.println(f(-1212))
