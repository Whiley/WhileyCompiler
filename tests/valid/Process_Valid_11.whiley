

type state is ({int y, int x} s) where s.x < s.y

type pState is &state

method send2(pState _this, int x) -> int:
    return _this->x + _this->y

public export method test() :
    int x = send2(new {y: 2, x: 1},1)
    assume x == 3
