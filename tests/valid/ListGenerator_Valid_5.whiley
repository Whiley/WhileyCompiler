import whiley.lang.System

function f([int] x) => string:
    return Any.toString(|x|)

method main(System.Console sys) => void:
    arr = []
    sys.out.println(f(arr))
