import println from whiley.lang.System

type nat is int

type expr is nat | {int op, expr left, expr right}

method main(System.Console sys) => void:
    e = 14897
    sys.out.println(Any.toString(e))
