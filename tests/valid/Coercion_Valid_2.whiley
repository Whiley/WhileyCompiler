import whiley.lang.System

function f(ASCII.string x) -> [int]:
    return ([int]) x

method main(System.Console sys) -> void:
    sys.out.println(f("Hello World"))
