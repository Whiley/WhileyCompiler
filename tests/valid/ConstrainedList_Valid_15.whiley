import whiley.lang.System

function f() -> ASCII.string:
    return ""

method main(System.Console sys) -> void:
    sys.out.println(f())
