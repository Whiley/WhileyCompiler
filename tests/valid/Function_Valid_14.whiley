

function f(int x) -> int:
    return 1

function f(real y) -> int:
    return 2

function f(int[] xs) -> int:
    return 3

public export method test() :
    assume f(1) == 1
    assume f(1.234) == 2
    assume f([1, 2, 3]) == 3
