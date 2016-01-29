
function f(int[] xs) -> bool
requires xs != [0;0]:
    return true

method main() :
    f([1, 4])
    f([0;0])
