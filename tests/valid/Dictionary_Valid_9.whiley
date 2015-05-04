

type DL1 is {int=>int}

type DL2 is {any=>int}

function update(DL1 ls) -> DL2:
    any rs = ls
    rs[1.2] = 1
    return rs

public export method test() -> void:
    DL1 x = {0=>1, 1=>2}
    DL2 y = update(x)
    assume y == {0=>1, 1=>2, 1.2=>1}
