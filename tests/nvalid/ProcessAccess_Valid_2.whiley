import * from whiley.lang.System

type state is {int y, int x, SystemOutWriter out}

type pState is ref state

method send(pState this, int z) => void:
    this->out.println(Any.toString(this->x))
    this->out.println(Any.toString(this->y))
    this->out.println(Any.toString(z))

method main(System.Console sys) => void:
    ps = new {y: 2, x: 1, out: sys.out}
    ps.send(1)
