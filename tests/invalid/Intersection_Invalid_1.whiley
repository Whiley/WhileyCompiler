type EmptyList is int[] & bool[]

function size(EmptyList l) -> int:
    return |l|

function f(int[] x) -> int:
    return size(x)
