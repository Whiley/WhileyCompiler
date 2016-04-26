type state is {int y, int x}

type pState is &state

method send(pState _this, int z) :
    assume _this->x == 1
    assume _this->y == 2
    assume z == 1

public export method test() :
    pState ps = new {y: 2, x: 1}
    send(ps,1)
