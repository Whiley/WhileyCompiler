

type DL1 is {int=>int}

type DL2 is {int=>real}

function update(DL1 ls) -> DL2:
    any rs = ls
    rs[0] = 1.234
    return (DL2) rs

public export method test() -> void:
    DL1 x = {0=>1, 1=>2}
    DL2 y = update(x)
    assume y == {0=>1.234, 1=>2.0}

