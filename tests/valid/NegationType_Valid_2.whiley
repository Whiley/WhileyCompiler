

function f(int x) -> !null:
    return x

public export method test() :
    assume f(1) == 1
