import whiley.lang.*

type state is {int y, int x}

type pState is &state

method send(pState this, int x, System.Console sys) -> void:
    this->x = x
    assert this->x == x
    sys.out.println(*this)
    sys.out.println_s("sent")
    sys.out.println(x)

method main(System.Console sys) -> void:
    pState ps = new {y: 2, x: 1}
    send(ps, 1, sys)
