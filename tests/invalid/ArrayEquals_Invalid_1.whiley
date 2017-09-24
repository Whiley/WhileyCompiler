
function f(int[] xs) -> bool
requires xs != [0;0]:
    return true

public export method test() :
    f([1, 4])
    f([0;0])
