import whiley.lang.System

type realtup is {real op}

function f(realtup t) => string:
    real x = t.op
    return Any.toString(t)

method main(System.Console sys) => void:
    {int op} t = {op: 1}
    sys.out.println(f(t))
