

type state is {int y, int x}

type pState is &state

method send2(pState this, int x) -> int:
    return this->x + this->y

public export method test() :
    int x = send2(new {y: 2, x: 1},1)
    assume x == 3
