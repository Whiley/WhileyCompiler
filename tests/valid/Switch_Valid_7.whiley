import println from whiley.lang.System

type nat is int where $ >= 0

function f(int x) => int
requires x >= 0
ensures ($ == 0) || ($ == 1):
    switch x:
        case 1:
            return 1
        default:
            return 0

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f(2)))
    sys.out.println(Any.toString(f(1)))
    sys.out.println(Any.toString(f(0)))
