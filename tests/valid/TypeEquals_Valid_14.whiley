import whiley.lang.System

type test is {int x} | {int y}

type src is test | int

function f(src e) => string:
    if e is test:
        return "{int x} | {int y}"
    else:
        return "int"

method main(System.Console sys) => void:
    sys.out.println(f({x: 1}))
    sys.out.println(f({y: 2}))
    sys.out.println(f(1))
