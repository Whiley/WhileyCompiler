import println from whiley.lang.System

function extract([int] ls) => [int]:
    i = 0
    r = [1]
    while i < |ls|:
        r = r ++ [ls[i]]
        i = i + 1
    return r

method main(System.Console sys) => void:
    rs = extract([-2, -3, 1, 2, -23, 3, 2345, 4, 5])
    sys.out.println(Any.toString(rs))
