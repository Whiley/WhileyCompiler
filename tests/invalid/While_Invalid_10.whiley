
function extract([int] ls) => [int]:
    int i = 0
    [int] r = []
    //
    while i < |ls| where |r| < 2:
        r = r ++ [ls[i]]
        i = i + 1
    return r

method main(System.Console sys) => void:
    [int] rs = extract([-2, -3, 1, 2, -23, 3, 2345, 4, 5])
    //
    debug Any.toString(rs)
