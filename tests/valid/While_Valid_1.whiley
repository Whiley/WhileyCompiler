

function reverse([int] ls) -> [int]:
    int i = |ls|
    [int] r = []
    while i > 0 where i <= |ls|:
        i = i - 1
        r = r ++ [ls[i]]
    return r

public export method test() -> void:
    [int] rs = reverse([1, 2, 3, 4, 5])
    assume rs == [5,4,3,2,1]
