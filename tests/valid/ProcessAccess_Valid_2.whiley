type state is {int y, int x}

type pState is &state

method send(pState this, int z) -> void:
    assume this->x == 1
    assume this->y == 2
    assume z == 1

public export method test() -> void:
    pState ps = new {y: 2, x: 1}
    send(ps,1)
