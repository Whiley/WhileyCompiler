import whiley.lang.System

function f(string x) => !null & !int:
    return x

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f("Hello World")))
