type rec1 is {int x}
type rec2 is {rec1 current}

function f(rec2 r) -> rec2:
    r.current.x = 1
    return r

public export method test() :
    rec2 r = {current: {x: 0}}
    assume f(r) == {current: {x: 1}}
