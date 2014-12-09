
function extract([int] ls, [int] rs) -> [int]:
    int i = 0
    int r = [1]
    //
    while i < |ls| where |r| > 0:
        r = rs
        i = i + 1
    //
    return r

method main(System.Console sys) -> void:
    [int] rs
    //
    rs = extract([-2, -3, 1, 2, -23, 3, 2345, 4, 5], [])
    debug Any.toString(rs)
