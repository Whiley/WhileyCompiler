type intr is int|real

function f(intr[] e) -> int[]:
    if e is int[]:
        return e
    else:
        return [1, 2, 3]

public export method test() :
    assume f([1, 2, 3, 4, 5, 6, 7]) == [1, 2, 3, 4, 5, 6, 7]
    assume f([0;0]) == [0;0]
    assume f([1, 2, 2.01]) == [1,2,3]
    assume f([1.23, 2, 2.01]) == [1,2,3]
