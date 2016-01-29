

type pos is (bool r)

type neg is (int n) where n < 0

type expr is pos | neg

function f(expr e) -> bool:
    if e is pos:
        return true
    else:
        return false

public export method test() :
    assume f(-1) == false
    assume f(false) == true
    assume f(true) == true
    assume f(!true) == true
