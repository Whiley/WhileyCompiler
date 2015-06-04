

type state is {int y, int x}

type pState is &state

method send(pState this, int x) -> void:
    this->x = x
    assert this->x == x
    assume (*this) == {x: 3, y: 2}

public export method test() -> void:
    pState ps = new {y: 2, x: 1}
    send(ps, 3)
