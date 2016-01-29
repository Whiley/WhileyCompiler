

type tenup is (int x) where x > 10

type msg1 is {tenup op, int[] data}

type msg2 is {int index}

type msgType is msg1 | msg2

function f(msgType m) -> any:
    return m

public export method test() :
    msg1 x = {op: 11, data: [0;0]}
    assume f(x) == {op: 11, data: [0;0]}
