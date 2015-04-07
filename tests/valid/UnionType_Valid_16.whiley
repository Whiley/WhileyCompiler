import whiley.lang.*

type IntList is {int | [int] op}

method main(System.Console sys) -> void:
    IntList x = {op: 1}
    x.op = 1
    IntList y = x
    sys.out.println(y)
    x = {op: [1, 2, 3]}
    sys.out.println(x)
