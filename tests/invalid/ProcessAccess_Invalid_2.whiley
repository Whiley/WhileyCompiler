type state is {int y, int x}

type pState is &state

method f(pState _this) :
    _this = new {z: 4, x: 3}
