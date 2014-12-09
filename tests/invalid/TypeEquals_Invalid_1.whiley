import * from whiley.lang.*

type expr is [int] | int

method f(expr e) -> void:
    if e is [int]:
        debug ("GOT [INT]")
    else:
        if e is int:
            debug ("GOT INT")
        else:
            debug ("GOT SOMETHING ELSE?")

method main(System.Console sys) -> void:
    e = 1
    f(e)
    e = {y: 2, x: 1}
    f(e)
