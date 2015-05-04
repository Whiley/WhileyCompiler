

function extract([int] ls) -> ([int] result)
// Returned list cannot be empty
ensures |result| > 0:
    int i = 0
    [int] r = [1]
    while i < |ls| where |r| > 0:
        r = r ++ [1]
        i = i + 1
    return r

public export method test() -> void:
    [int] rs = extract([1, 2, 3, 4, 5, 6, 7])
    assume rs == [1, 1, 1, 1, 1, 1, 1, 1]
    rs = extract([])
    assume rs == [1]
