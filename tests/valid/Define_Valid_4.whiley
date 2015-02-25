import whiley.lang.System

type codeOp is (int x) where x in {1, 2, 3, 4}

type code is {codeOp op, [int] payload}

function f(codeOp x) -> string:
    code y = {op: x, payload: []}
    return Any.toString(y)

method main(System.Console sys) -> void:
    sys.out.println(f(1))
