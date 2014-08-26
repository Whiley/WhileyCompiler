import whiley.lang.System

type Rtypes is {real y, real x} | {int z, int x}

function f(Rtypes e) => string:
    if e is {int z, int x}:
        return "GOT IT"
    else:
        return "NOPE"

method main(System.Console sys) => void:
    sys.out.println(f({y: 1.2, x: 1.2}))
    sys.out.println(f({y: 1.0, x: 1.0}))
    sys.out.println(f({z: 1, x: 1}))
