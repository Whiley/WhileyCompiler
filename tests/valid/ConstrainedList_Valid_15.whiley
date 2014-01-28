import whiley.lang.System

function f() => string:
    return ""

method main(System.Console sys) => void:
    sys.out.println(f())
