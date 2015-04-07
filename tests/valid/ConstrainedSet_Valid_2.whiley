import whiley.lang.*

function f(int x) -> {int}:
    return {x}

method main(System.Console sys) -> void:
    {int} bytes = f(0)
    sys.out.println(bytes)
