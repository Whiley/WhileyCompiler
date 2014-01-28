import whiley.lang.System

type intlist is int | [int]

function f([intlist] l) => string:
    return Any.toString(l)

method main(System.Console sys) => void:
    [int|[int]] x
    
    if |sys.args| == 0:
        x = [1, 2, 3]
    else:
        x = [[1], [2, 3], [5]]
    x[0] = 1
    sys.out.println(f(x))
