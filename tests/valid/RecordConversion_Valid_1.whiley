import whiley.lang.*

type realtup is {real op}

function f(realtup t) -> real:
    real x = t.op
    return x

method main(System.Console sys) -> void:
    {int op} t = {op: 1}
    sys.out.println(f((realtup) t))
