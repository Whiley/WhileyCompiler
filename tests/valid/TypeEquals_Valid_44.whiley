

type imsg is int | {int op} | {int[] msg}

function getMessage(imsg m) -> any:
    if m is {int[] msg}:
        return m.msg
    else:
        if m is {int op}:
            return m.op
        else:
            return m

public export method test() :
    assume getMessage({msg: "HELLO WORLD"}) == "HELLO WORLD"
    assume getMessage(1) == 1
    assume getMessage({op: 123}) == 123
