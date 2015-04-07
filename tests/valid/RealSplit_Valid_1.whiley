import whiley.lang.*

function f(real z) -> (int, int):
    int x / int y = z
    return (x, y)

method main(System.Console sys) -> void:
    sys.out.println(f(10.0 / 5.0))
    sys.out.println(f(10.0 / 4.0))
    sys.out.println(f(1.0 / 4.0))
    sys.out.println(f(103.0 / 2.0))
    sys.out.println(f(-10.0 / 5.0))
    sys.out.println(f(-10.0 / 4.0))
    sys.out.println(f(-1.0 / 4.0))
    sys.out.println(f(-103.0 / 2.0))
    sys.out.println(f(-10.0 / -5.0))
    sys.out.println(f(-10.0 / -4.0))
    sys.out.println(f(-1.0 / -4.0))
    sys.out.println(f(-103.0 / -2.0))
    sys.out.println(f(10.0 / -5.0))
    sys.out.println(f(10.0 / -4.0))
    sys.out.println(f(1.0 / -4.0))
    sys.out.println(f(103.0 / -2.0))
