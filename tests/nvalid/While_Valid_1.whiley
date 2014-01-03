import println from whiley.lang.System

function reverse([int] ls) => [int]:
    i = |ls|
    r = []
    while i > 0:
        i = i - 1
        r = r + [ls[i]]
    return r

method main(System.Console sys) => void:
    rs = reverse([1, 2, 3, 4, 5])
    sys.out.println(Any.toString(rs))
