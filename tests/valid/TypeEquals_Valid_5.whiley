

function f(int | null x) -> bool:
    if x is null:
        return true
    else:
        return false

public export method test() -> void:
    int|null x = null
    assume f(x) == true
    assume f(1) == false
