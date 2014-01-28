import whiley.lang.System

type IntList is {int | [int] op}

method main(System.Console sys) => void:
    x = {op: 1}
    x.op = 1
    y = x
    sys.out.println(Any.toString(y))
    x = {op: [1, 2, 3]}
    sys.out.println(Any.toString(x))
