import whiley.lang.*

type tenup is (int x) where x > 10

type msg1 is {tenup op, [int] data}

type msg2 is {int index}

type msgType is msg1 | msg2

function f(msgType m) -> msgType:
    return m

method main(System.Console sys) -> void:
    msg1 m1 = {op: 11, data: []}
    msg2 m2 = {index: 1}
    sys.out.println(f(m1))
    sys.out.println(f(m2))
