type intr is int|bool

function f(intr[] e) -> int[]:
    if e is int[]:
        return e
    else:
        return [1, 2, 3]

public export method test() :
    assume f([1, 2, 3, 4, 5, 6, 7]) == [1, 2, 3, 4, 5, 6, 7]
    assume f([0;0]) == [0;0]
    assume f([1, 2, true]) == [1,2,3]
    assume f([false, 2, true]) == [1,2,3]
