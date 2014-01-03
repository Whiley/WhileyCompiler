import println from whiley.lang.System

type state is {int y, int x}

type pState is ref state

method send(pState this, int x, System.Console sys) => void:
    this->x = x
    assert this->x == x
    sys.out.println(Any.toString(*this))
    sys.out.println("sent")
    sys.out.println(Any.toString(x))

method main(System.Console sys) => void:
    ps = new {y: 2, x: 1}
    ps.send(1, sys)
