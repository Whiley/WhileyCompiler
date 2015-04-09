import whiley.lang.*

type test is {int x} | {int y}

type src is test | int

function f(src e) -> bool:
    if e is test:
        return true
    else:
        return false

method main(System.Console sys) -> void:
    assume f({x: 1}) == true
    assume f({y: 2}) == true
    assume f(1) == false
