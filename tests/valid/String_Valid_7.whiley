import whiley.lang.*

function f([int] str, int end) -> [int]:
    return str[0..end]

method main(System.Console sys) -> void:
    [int] str = "Hello Cruel World"
    sys.out.println_s(f(str, 0))
    sys.out.println_s(f(str, 1))
    sys.out.println_s(f(str, 5))
    sys.out.println_s(f(str, 10))
