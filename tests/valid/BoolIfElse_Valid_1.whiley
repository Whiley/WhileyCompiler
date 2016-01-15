

function f(bool b) -> bool:
    if b:
        return true
    else:
        return false

public export method test() :
    assume f(true)
    assume !f(false)
