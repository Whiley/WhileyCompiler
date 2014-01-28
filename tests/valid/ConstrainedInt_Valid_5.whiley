import whiley.lang.System

type nat is int

function f() => nat:
    return 1

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f()))
