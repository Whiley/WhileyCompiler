

type imsg is int | {int[] msg}

function getMessage(imsg m) -> any:
    if m is {int[] msg}:
        return m.msg
    else:
        return m

public export method test() :
    assume getMessage({msg: "HELLO WORLD"}) == "HELLO WORLD"
    assume getMessage(1) == 1
