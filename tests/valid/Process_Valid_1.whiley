

type state is {int y, int x}

type pState is &state

method send(pState _this, int x) :
    _this->x = x
    assert _this->x == x
    assume (*_this) == {x: 3, y: 2}

public export method test() :
    pState ps = new {y: 2, x: 1}
    send(ps, 3)
