

type EmptyList is int[] & real[]

function size(EmptyList l) -> int:
    return |l|

public export method test() :
    assume size([0;0]) == 0
