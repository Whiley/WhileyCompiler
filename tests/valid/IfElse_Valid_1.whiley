import whiley.lang.System

function f(int x) => string:
    if x < 10:
        return "LESS THAN"
    else:
        if x > 10:
            return "GREATER THAN"
        else:
            return "EQUALS"

method main(System.Console sys) => void:
    sys.out.println(f(1))
    sys.out.println(f(10))
    sys.out.println(f(11))
    sys.out.println(f(1212))
    sys.out.println(f(-1212))
