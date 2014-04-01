import whiley.lang.System

function f(bool b) => string:
    if b:
        return "TRUE"
    else:
        return "FALSE"

method main(System.Console sys) => void:
    sys.out.println(f(true))
    sys.out.println(f(false))
