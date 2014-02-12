import whiley.lang.System

type state is {int y, int x}

type pState is &state

method send(pState this, int x, System.Console sys) => void:
    this->x = x
    assert this->x == x
    sys.out.println(Any.toString(*this))
    sys.out.println("sent")
    sys.out.println(Any.toString(x))

method main(System.Console sys) => void:
    pState ps = new {y: 2, x: 1}
    send(ps, 1, sys)
