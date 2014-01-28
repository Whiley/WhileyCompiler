import whiley.lang.System

function f(int x) => (int r)
// Return is between 0 and 2
ensures r >= 0 && r <= 2:
    //
    switch x:
        case 1:
            return 0
        case 2:
            return 1
    return 2

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f(2)))
    sys.out.println(Any.toString(f(1)))
    sys.out.println(Any.toString(f(0)))
