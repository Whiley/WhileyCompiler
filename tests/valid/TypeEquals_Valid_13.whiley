

type iset is {int} | int

function f(iset e) -> bool:
    if e is {int}:
        return true
    else:
        return false

public export method test() -> void:
    assume f({1, 2, 3}) == true
    assume f(1) == false
