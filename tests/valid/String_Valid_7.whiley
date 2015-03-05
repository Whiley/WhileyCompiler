import whiley.lang.System

function f(ASCII.string str, int end) -> ASCII.string:
    return str[0..end]

method main(System.Console sys) -> void:
    ASCII.string str = "Hello Cruel World"
    sys.out.println(f(str, 0))
    sys.out.println(f(str, 1))
    sys.out.println(f(str, 5))
    sys.out.println(f(str, 10))
