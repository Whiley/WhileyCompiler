

type posintlist is ([int] list) where no { x in list | x < 0 }

function sum(posintlist ls, int i) -> (int r)
// Input i must be valid index in list, or one past
requires i >= 0 && i <= |ls|
// Return cannot be negative
ensures r >= 0:
    //
    if i == |ls|:
        return 0
    else:
        return ls[i] + sum(ls, i + 1)

function sum(posintlist ls) -> (int r)
ensures r >= 0:
    //
    return sum(ls, 0)

public export method test() -> void:
    assume sum([1, 2, 3, 4, 5, 6, 7]) == 28
