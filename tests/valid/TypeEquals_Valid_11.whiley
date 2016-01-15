

type Rtypes is {real y, real x} | {int z, int x}

function f(Rtypes e) -> bool:
    if e is {int z, int x}:
        return true
    else:
        return false

public export method test() :
    assume f({y: 1.2, x: 1.2}) == false
    assume f({y: 1.0, x: 1.0}) == false
    assume f({z: 1, x: 1}) == true
