import whiley.lang.*

function indexOf(int c1, [int] str) -> int | null:
    int i = 0
    for c2 in str:
        if c1 == c2:
            return i
        i = i + 1
    return null

method main(System.Console sys) -> void:
    sys.out.println(indexOf('H', "Hello World"))
    sys.out.println(indexOf('e', "Hello World"))
    sys.out.println(indexOf('l', "Hello World"))
    sys.out.println(indexOf('o', "Hello World"))
    sys.out.println(indexOf(' ', "Hello World"))
    sys.out.println(indexOf('W', "Hello World"))
    sys.out.println(indexOf('r', "Hello World"))
    sys.out.println(indexOf('d', "Hello World"))
    sys.out.println(indexOf('z', "Hello World"))
    sys.out.println(indexOf('1', "Hello World"))
