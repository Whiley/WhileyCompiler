import whiley.lang.System

function f(string x) => [int]:
    return x

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f("Hello World")))
