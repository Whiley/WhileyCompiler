import println from whiley.lang.System

function f([int] xs) => int:
    return |xs|

method main(System.Console sys) => void:
    left = [1, 2, 3]
    right = [5, 6, 7]
    r = f(left ++ right)
    sys.out.println(Any.toString(r))
