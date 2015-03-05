import whiley.lang.System

type imsg is int | {ASCII.string msg}

function getMessage(imsg m) -> ASCII.string:
    if m is {ASCII.string msg}:
        return m.msg
    else:
        return Any.toString(m)

method main(System.Console sys) -> void:
    sys.out.println(getMessage({msg: "HELLO WORLD"}))
    sys.out.println(getMessage(1))
