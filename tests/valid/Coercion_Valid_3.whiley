import whiley.lang.System

function f(ASCII.char x) -> int:
    return (int) x

method main(System.Console sys) -> void:
    sys.out.println(f('H'))
