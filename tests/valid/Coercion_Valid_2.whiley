import whiley.lang.*

function f([int] x) -> [int]:
    return x

method main(System.Console sys) -> void:
    assume f("Hello World") == [72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100]