type EmptyList is ((null|int[]) & (null|bool[]) t)

function size(EmptyList l) -> int:
    return |l|

function f(int[] x) -> int:
    return size(x)
