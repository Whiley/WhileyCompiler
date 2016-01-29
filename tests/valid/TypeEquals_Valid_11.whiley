

type Rtypes is {bool y, bool x} | {int z, int x}

function f(Rtypes e) -> bool:
    if e is {int z, int x}:
        return true
    else:
        return false

public export method test() :
    assume f({y: false, x: true}) == false
    assume f({y: true, x: true}) == false
    assume f({z: 1, x: 1}) == true
