import println from whiley.lang.System

type nat is int where $ >= 0

function f([int] xs) => nat:
    return |xs|

method main(System.Console sys) => void:
    rs = f([1, 2, 3])
    sys.out.println(Any.toString(rs))
    rs = f([])
    sys.out.println(Any.toString(rs))
