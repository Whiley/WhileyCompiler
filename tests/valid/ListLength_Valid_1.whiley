

type nat is (int x) where x >= 0

function f(int[] xs) -> nat:
    return |xs|

public export method test() :
    assume f([1, 2, 3]) == 3
    assume f([0;0]) == 0
