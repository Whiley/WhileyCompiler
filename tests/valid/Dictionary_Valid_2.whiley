import whiley.lang.System

function f(int x) => {int=>int}:
    return {1=>x, 3=>2}

function get(int i, {int=>int} map) => int:
    return map[i]

method main(System.Console sys) => void:
    sys.out.println(Any.toString(get(1, f(1))))
    sys.out.println(Any.toString(get(1, f(2))))
    sys.out.println(Any.toString(get(1, f(3))))
    sys.out.println(Any.toString(get(3, f(3))))
