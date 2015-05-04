

type DL is {int=>[real]}

function update(DL ls) -> DL
requires |ls[0]| > 0:
    //
    ls[0][0] = 1.234
    return ls

public export method test() -> void:
    DL x = {0=>[1.0, 2.0, 3.0], 1=>[3.4]}
    x = update(x)
    assume x == {0=>[1.234, 2.0, 3.0], 1=>[3.4]}
