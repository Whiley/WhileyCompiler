

function select({int} xs) -> (int r)
// Input list cannot be empty
requires |xs| > 0
// Returned value must be in input
ensures r in xs:
    //
    for x in xs:
        return x
    return 0

public export method test() -> void:
    assume select({1, 2, 3, 4, 5, 6, 7, 8, 9, 10}) == 1
