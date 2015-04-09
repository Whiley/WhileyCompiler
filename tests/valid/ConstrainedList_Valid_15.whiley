import whiley.lang.*

function f() -> [int]:
    return ""

method main(System.Console sys) -> void:
    assume f() == ""
