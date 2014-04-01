import whiley.lang.System

function f(string s, char c) => string:
    return s ++ c

method main(System.Console sys) => void:
    sys.out.println(f("Hello Worl", 'd'))
