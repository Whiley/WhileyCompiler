import whiley.lang.*

function f([real] x) -> {real}:
    return ({real}) x

method main(System.Console sys) -> void:
    {real} x = f([2.2, 3.3])
    sys.out.println(x)
