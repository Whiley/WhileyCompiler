
function extract([int] ls, [int] r) => [int]:
    int i = 0
    //
    while i < |ls| where |r| > 0:
        r = r ++ [ls[i]]
        i = i + 1
    //
    return r

method main(System.Console sys) => void:
    [int] rs 
    //
    rs = extract([-2, -3, 1, 2, -23, 3, 2345, 4, 5], [1])
    debug Any.toString(rs)
    rs = extract([-2, -3, 1, 2, -23, 3, 2345, 4, 5], [])
    debug Any.toString(rs)
