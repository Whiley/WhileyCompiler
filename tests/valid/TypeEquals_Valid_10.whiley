

type Rtypes is {int y, int x} | {int z, int x}

function f(Rtypes e) -> bool:
    if e is {int y, int x}:
        return true
    else:
        return false

public export method test() :
    assume f({y: 1, x: 3}) == true
    assume f({z: 1, x: 3}) == false
