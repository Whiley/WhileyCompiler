import println from whiley.lang.System

function f(int x) => {int}:
    return {x}

method main(System.Console sys) => void:
    bytes = f(0)
    sys.out.println(Any.toString(bytes))
