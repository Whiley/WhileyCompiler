import whiley.lang.*

type btup is {int index, int op}

function f(btup b) -> [int]:
    return [b.op, b.index]

method main(System.Console sys) -> void:
    sys.out.println(f({index: 2, op: 1}))
