import println from whiley.lang.System

function extract([int] ls) => [int]
ensures |$| > 0:
    i = 0
    r = [1]
    while i < |ls| where |r| > 0:
        r = r + [1]
        i = i + 1
    return r

method main(System.Console sys) => void:
    rs = extract([1, 2, 3, 4, 5, 6, 7])
    sys.out.println(Any.toString(rs))
    rs = extract([])
    sys.out.println(Any.toString(rs))
