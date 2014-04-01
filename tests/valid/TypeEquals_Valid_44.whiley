import whiley.lang.System

type imsg is int | {int op} | {string msg}

function getMessage(imsg m) => string:
    if m is {string msg}:
        return m.msg
    else:
        if m is {int op}:
            return Any.toString(m.op)
        else:
            return Any.toString(m)

method main(System.Console sys) => void:
    sys.out.println(getMessage({msg: "HELLO WORLD"}))
    sys.out.println(getMessage(1))
    sys.out.println(getMessage({op: 123}))
