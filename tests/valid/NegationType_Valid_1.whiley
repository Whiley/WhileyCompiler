

function f(any x) -> !null:
    if x is null:
        return 1
    else:
        return x

public export method test() :
    assume f(1) == 1
    assume f([1, 2, 3]) == [1,2,3]
