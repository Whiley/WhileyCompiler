import whiley.lang.*

method main(System.Console sys) -> void:
    int x = 112233445566778899
    sys.out.println(x)
    x = x + 1
    sys.out.println(x)
    x = x - 556
    sys.out.println(x)
    x = x * 123
    sys.out.println(x)
    x = x / 2
    sys.out.println(x)
