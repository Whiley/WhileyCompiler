

function f(bool b) -> bool:
    return b

public export method test() :
    bool x = true
    assume f(x)
    x = false
    assume !f(x)
