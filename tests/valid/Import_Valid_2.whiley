import whiley.lang.*

function f([int] x) -> int:
    return x[0]

public method main(System.Console sys) -> void:
    assume f("1") == '1'
