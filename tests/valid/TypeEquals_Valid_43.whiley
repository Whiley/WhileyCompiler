import whiley.lang.*

type imsg is int | {[int] msg}

function getMessage(imsg m) -> any:
    if m is {[int] msg}:
        return m.msg
    else:
        return m

method main(System.Console sys) -> void:
    sys.out.println(getMessage({msg: "HELLO WORLD"}))
    sys.out.println(getMessage(1))
