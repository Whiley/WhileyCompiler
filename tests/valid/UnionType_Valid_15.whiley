type msg1 is {int op, int[] data}
type msg2 is {int op, {int dum}[] data}

type msgType is msg1 | msg2

function f(msgType m) -> msgType:
    return m

public export method test() :
    msg1 x = {op: 1, data: [1, 2, 3]}
    assume f(x) == {op: 1, data: [1, 2, 3]}
    int[] list = x.data
    assume list == [1,2,3]
