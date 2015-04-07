import whiley.lang.*

function g(int x) -> int:
    if (x <= 0) || (x >= 125):
        return 1
    else:
        return x

function f(int x) -> {int}:
    return {g(x)}

method main(System.Console sys) -> void:
    {int} bytes = f(0)
    sys.out.println(bytes)
