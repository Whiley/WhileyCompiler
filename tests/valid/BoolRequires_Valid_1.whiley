

function f(bool[] x) -> bool[]
requires (|x| > 0) && x[0]:
    return x

public export method test() :
    assume f([true]) == [true]
    assume f([true, false]) == [true, false]
    assume f([true, false, true]) == [true, false, true]
