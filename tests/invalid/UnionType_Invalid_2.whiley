type IntBool is int | bool

function f(int y) -> int:
    return y

public export method test():
    IntBool x
    //
    x = 123
    f(x)
    x = false
    f(x)
