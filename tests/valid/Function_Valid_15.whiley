

type msg1 is {int op, int s} where op == 1

type msg2 is {int op, int s} where op == 2

function f(msg1 m) -> int:
    return 1

function f(msg2 m) -> int:
    return 2

public export method test() :
    msg1 m1 = {op: 1, s: 123}
    msg1 m2 = {op: 2, s: 123}
    assume f(m1) == 1
    assume f(m2) == 2
