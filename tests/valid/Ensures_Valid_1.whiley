import println from whiley.lang.System

function add(int x, int y) => int
requires (x >= 0) && (y >= 0)
ensures $ > 0:
    if x == y:
        return 1
    else:
        return x + y

method main(System.Console sys) => void:
    sys.out.println(Any.toString(1))
