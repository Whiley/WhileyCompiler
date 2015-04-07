import whiley.lang.*

type Rtypes is {int y, int x} | {int z, int x}

function f(Rtypes e) -> bool:
    if e is {int y, int x}:
        return true
    else:
        return false

method main(System.Console sys) -> void:
    sys.out.println(f({y: 1, x: 1}))
    sys.out.println(f({z: 1, x: 1}))
