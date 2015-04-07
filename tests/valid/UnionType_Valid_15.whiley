import whiley.lang.*

type msg1 is {int op, [int] data}

type msg2 is {int op, [{int dum}] data}

type msgType is msg1 | msg2

function f(msgType m) -> msgType:
    return m

method main(System.Console sys) -> void:
    msg1 x = {op: 1, data: [1, 2, 3]}
    sys.out.println(f(x))
    [int] list = x.data
    sys.out.println(list)
