

function f(int x) -> int[]:
    return [x]

public export method test() :
    assume f(0) == [0]
