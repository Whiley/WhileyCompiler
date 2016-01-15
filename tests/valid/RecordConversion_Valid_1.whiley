

type realtup is {real op}

function f(realtup t) -> real:
    real x = t.op
    return x

public export method test() :
    {int op} t = {op: 1}
    assume f((realtup) t) == 1.0
