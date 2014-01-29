import whiley.lang.System

type msg1 is {int op, [int] data}

type msg2 is {int op, [{int dum}] data}

type msgType is msg1 | msg2

function f(msgType m) => string:
    return Any.toString(m)

method main(System.Console sys) => void:
    msg1 x = {op: 1, data: [1, 2, 3]}
    sys.out.println(f(x))
    [int] list = x.data
    sys.out.println(Any.toString(list))
