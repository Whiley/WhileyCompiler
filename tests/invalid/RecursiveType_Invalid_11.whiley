type T is {T t}

function f(int|T x) -> int:
    return x

public export method test() :
    assume f(1) == 1
