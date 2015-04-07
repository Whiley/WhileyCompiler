import * from whiley.lang.*

type state is {int y, int x, System.Console console}

type pState is &state

method send(pState this, int z) -> void:
    this->console.out.println(this->x)
    this->console.out.println(this->y)
    this->console.out.println(z)

method main(System.Console sys) -> void:
    pState ps = new {y: 2, x: 1, console: sys}
    send(ps,1)
