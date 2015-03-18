import whiley.lang.System

function f(bool b) -> ASCII.string:
    if b:
        return "TRUE"
    else:
        return "FALSE"

method main(System.Console sys) -> void:
    sys.out.println_s(f(true))
    sys.out.println_s(f(false))
