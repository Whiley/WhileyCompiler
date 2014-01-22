import println from whiley.lang.System

function f(int x) => int
ensures $ != 1:
    switch x:
        case 1:
            return 2
        case 2:
            return 2
    return x

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f(2)))
    sys.out.println(Any.toString(f(1)))
    sys.out.println(Any.toString(f(0)))
