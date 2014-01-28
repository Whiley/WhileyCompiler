import * from whiley.lang.System

type state is {int y, int x, System.Console console}

type pState is &state

method send(pState this, int z) => void:
    this->console.out.println(Any.toString(this->x))
    this->console.out.println(Any.toString(this->y))
    this->console.out.println(Any.toString(z))

method main(System.Console sys) => void:
    pState ps = new {y: 2, x: 1, console: sys}
    send(ps,1)
