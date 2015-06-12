type list is [int]

function index(list l, int index) -> any:
    return l[index]

public export method test() -> void:
    [int] l = [1, 2, 3]
    assume index(l,0) == 1
    assume index(l,1) == 2
    assume index(l,2) == 3
