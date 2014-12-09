
type neg is (int n) where n < 0

type pos is (int n) where n > 0

type exp1 is neg | {exp1 rest}

type exp2 is pos | {exp2 rest}

function f(exp1 e1) -> exp2:
    return e1

method main(System.Console sys) -> void:
    x = f(-1)
    debug Any.toString(x)
