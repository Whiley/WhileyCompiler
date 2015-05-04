

type nat is (int x) where x < 10

function f() -> nat:
    return 1

public export method test() -> void:
    assume f() == 1
