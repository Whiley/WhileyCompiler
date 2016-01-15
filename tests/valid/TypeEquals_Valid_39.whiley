

type pos is (real r) where r > 0.0

type neg is (int n) where n < 0

type expr is pos | neg

function f(expr e) -> bool:
    if e is pos:
        return true
    else:
        return false

public export method test() :
    assume f(-1) == false
    assume f(1.0) == true
    assume f(1234.0) == true
    assume f(-1.0) == false
