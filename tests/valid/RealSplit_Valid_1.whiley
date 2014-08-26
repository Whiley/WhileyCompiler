import whiley.lang.System

function f(real z) => (int, int):
    int x / int y = z
    return (x, y)

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f(10.0 / 5.0)))
    sys.out.println(Any.toString(f(10.0 / 4.0)))
    sys.out.println(Any.toString(f(1.0 / 4.0)))
    sys.out.println(Any.toString(f(103.0 / 2.0)))
    sys.out.println(Any.toString(f(-10.0 / 5.0)))
    sys.out.println(Any.toString(f(-10.0 / 4.0)))
    sys.out.println(Any.toString(f(-1.0 / 4.0)))
    sys.out.println(Any.toString(f(-103.0 / 2.0)))
    sys.out.println(Any.toString(f(-10.0 / -5.0)))
    sys.out.println(Any.toString(f(-10.0 / -4.0)))
    sys.out.println(Any.toString(f(-1.0 / -4.0)))
    sys.out.println(Any.toString(f(-103.0 / -2.0)))
    sys.out.println(Any.toString(f(10.0 / -5.0)))
    sys.out.println(Any.toString(f(10.0 / -4.0)))
    sys.out.println(Any.toString(f(1.0 / -4.0)))
    sys.out.println(Any.toString(f(103.0 / -2.0)))
