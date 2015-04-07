import whiley.lang.*

function f(int x) -> int:
    if x < 10:
        return 1
    else:
        return 2

method main(System.Console sys) -> void:
    sys.out.println(f(1))
    sys.out.println(f(10))
    sys.out.println(f(11))
    sys.out.println(f(1212))
    sys.out.println(f(-1212))
