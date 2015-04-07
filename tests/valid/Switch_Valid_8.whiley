import whiley.lang.*

method main(System.Console sys) -> void:
    int x = 1
    switch x:
        case 1:
            sys.out.println_s("CASE 1")
            return
        case 2:
            sys.out.println_s("CASE 2")
            return
    sys.out.println_s("DEFAULT CASE")
