import whiley.lang.System

type imsg is int | {string msg}

function getMessage(imsg m) => string:
    if m is {string msg}:
        return m.msg
    else:
        return Any.toString(m)

method main(System.Console sys) => void:
    sys.out.println(getMessage({msg: "HELLO WORLD"}))
    sys.out.println(getMessage(1))
