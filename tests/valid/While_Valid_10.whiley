import whiley.lang.System

function extract([int] ls) => [int]:
    int i = 0
    [int] r = [1]
    while i < |ls|:
        r = r ++ [1]
        i = i + 1
    return r

method main(System.Console sys) => void:
    [int] rs = extract([1, 2, 3, 4, 5, 6, 7])
    sys.out.println(Any.toString(rs))
    rs = extract([])
    sys.out.println(Any.toString(rs))
