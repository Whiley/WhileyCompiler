import whiley.lang.System

function f(ASCII.string x) -> !null & !int:
    return x

method main(System.Console sys) -> void:
    sys.out.println(f("Hello World"))
