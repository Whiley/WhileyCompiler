
type pos is (int x) where x > 0

type neg is (int x) where x < 0

type expr is pos | neg

function g(neg x) -> bool:
    return false

function f(expr e) :
    if e is pos:
        g((neg) e)

public export method test() :
    f(-1)
    f(1)
