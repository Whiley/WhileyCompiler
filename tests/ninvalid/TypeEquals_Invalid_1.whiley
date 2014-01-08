import * from whiley.lang.*

type expr is [int] | int

method f(System this, expr e) => void:
    if e is [int]:
        sys.out.println("GOT [INT]")
    else:
        if e is int:
            sys.out.println("GOT INT")
        else:
            sys.out.println("GOT SOMETHING ELSE?")

method main(System.Console sys) => void:
    e = 1
    this.f(e)
    e = {y: 2, x: 1}
    this.f(e)
