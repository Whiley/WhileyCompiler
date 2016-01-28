

type IntBool is int | bool

function f(IntBool y) -> IntBool:
    return y

public export method test() :
    IntBool x = 123
    assume f(x) == 123
    x = true
    assume f(x) == true
