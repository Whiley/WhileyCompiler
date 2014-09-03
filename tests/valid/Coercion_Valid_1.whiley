import whiley.lang.System

function f(int x) => real:
    return (real) x

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f(123)))
