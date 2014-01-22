import println from whiley.lang.System

type realtup is {real op}

function f(realtup t) => string:
    x = t.op
    return Any.toString(t)

method main(System.Console sys) => void:
    t = {op: 1}
    sys.out.println(f(t))
