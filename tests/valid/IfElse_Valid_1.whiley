import whiley.lang.System

function f(int x) -> ASCII.string:
    if x < 10:
        return "LESS THAN"
    else:
        if x > 10:
            return "GREATER THAN"
        else:
            return "EQUALS"

method main(System.Console sys) -> void:
    sys.out.println_s(f(1))
    sys.out.println_s(f(10))
    sys.out.println_s(f(11))
    sys.out.println_s(f(1212))
    sys.out.println_s(f(-1212))
