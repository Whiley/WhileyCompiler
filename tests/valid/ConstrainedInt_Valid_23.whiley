

type cr2num is (int x) where x in {1, 2, 3, 4}

function f(cr2num x) -> int:
    int y = x
    return y

public export method test() -> void:
    assume f(3) == 3
