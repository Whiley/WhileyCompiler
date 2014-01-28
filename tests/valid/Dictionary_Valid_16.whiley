import whiley.lang.System

function f(int x) => {int=>int}:
    return {1=>x, 3=>2}

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f(1)))
    sys.out.println(Any.toString(f(2)))
    sys.out.println(Any.toString(f(3)))
