import whiley.lang.System

type Rtypes is {int y, int x} | {int z, int x}

function f(Rtypes e) => string:
    if e is {int y, int x}:
        return "GOT IT"
    else:
        return "NOPE"

method main(System.Console sys) => void:
    sys.out.println(f({y: 1, x: 1}))
    sys.out.println(f({z: 1, x: 1}))
