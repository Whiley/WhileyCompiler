

function f(bool b) -> bool:
    if b:
        return true
    else:
        return false

public export method test() -> void:
    assume f(true)
    assume !f(false)
