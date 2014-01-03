import println from whiley.lang.System

type nat is int where $ >= 0

function f([int] xs) => [nat]
requires |xs| == 0:
    return xs

method main(System.Console sys) => void:
    rs = f([])
    sys.out.println(Any.toString(rs))
