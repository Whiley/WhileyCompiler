

type nat is int

function f() -> nat:
    return 1

public export method test() -> void:
    assume f() == 1
