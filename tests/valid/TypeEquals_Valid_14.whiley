import whiley.lang.*

type test is {int x} | {int y}

type src is test | int

function f(src e) -> bool:
    if e is test:
        return true
    else:
        return false

method main(System.Console sys) -> void:
    sys.out.println(f({x: 1}))
    sys.out.println(f({y: 2}))
    sys.out.println(f(1))
