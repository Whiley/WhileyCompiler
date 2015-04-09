import whiley.lang.*

function f(bool b) -> bool:
    if b:
        return true
    else:
        return false

method main(System.Console sys) -> void:
    assume f(true)
    assume !f(false)
