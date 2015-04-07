import whiley.lang.*

function f(any x) -> !null:
    if x is null:
        return 1
    else:
        return x

method main(System.Console sys) -> void:
    sys.out.println(f(1))
    sys.out.println(f([1, 2, 3]))
