import whiley.lang.System

type realtup is {real op}

function f(realtup t) => string:
    int x = t.op
    return Any.toString(t)

method main(System.Console sys) => void:
    {int} t = {op: 1}
    sys.out.println(f(t))
