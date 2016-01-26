

function f(int[] xs) -> int[]:
    return xs

public export method test() :
    assume f([1, 4]) == [1,4]
    assume f([0;0]) == [0;0]
