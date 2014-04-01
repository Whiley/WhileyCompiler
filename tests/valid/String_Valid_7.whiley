import whiley.lang.System

function f(string str, int end) => string:
    return str[0..end]

method main(System.Console sys) => void:
    string str = "Hello Cruel World"
    sys.out.println(f(str, 0))
    sys.out.println(f(str, 1))
    sys.out.println(f(str, 5))
    sys.out.println(f(str, 10))
