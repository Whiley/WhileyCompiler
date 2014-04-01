import whiley.lang.System

function f([real] x) => {real}:
    return x

method main(System.Console sys) => void:
    {real} x = f([2.2, 3.3])
    sys.out.println(Any.toString(x))
