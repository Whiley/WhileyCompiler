import println from whiley.lang.System

type nat is int where $ >= 0

function f([[nat]] xs) => [nat]
requires |xs| > 0:
    return xs[0]

method main(System.Console sys) => void:
    rs = f([[1, 2, 3], [4, 5, 6]])
    sys.out.println(Any.toString(rs))
