

type fcode is (int x) where x in {1, 2, 3, 4}

function g(fcode f) -> int:
    return f

public export method test() -> void:
    int x = 1
    assume g(x) == 1
