

type DL1 is {int=>int}

type DL2 is {real=>int}

function update(DL1 ls) -> DL2:
    any rs = ls
    rs[1.2] = 1
    return (DL2) rs

public export method test() -> void:
    DL1 x = {0=>1, 1=>2}
    DL2 y = update(x)
    assume y == {0.0=>1, 1.0=>2, 1.2=>1}
