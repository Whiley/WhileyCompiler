import whiley.lang.*

function f([int] x) -> !null & !int:
    return x

method main(System.Console sys) -> void:
    assume f("Hello World") == "Hello World"
