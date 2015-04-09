import whiley.lang.*

function f(bool b) -> bool:
    return b

method main(System.Console sys) -> void:
    bool x = true
    assume f(x)
    x = false
    assume !f(x)
