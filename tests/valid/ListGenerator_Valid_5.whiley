import whiley.lang.System

function f([int] x) -> ASCII.string:
    return Any.toString(|x|)

method main(System.Console sys) -> void:
    [int] arr = []
    sys.out.println(f(arr))
