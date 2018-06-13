type msgType1 is ({int op, int[] payload} r) where r.op == 1

type msgType2 is ({int op, int[] rest, int header} r) where r.op == 2

type msgType is msgType1 | msgType2

function f(msgType msg) -> int:
    if msg is msgType1:
        return msg.op
    else:
        return msg.op

public export method test() :
    assume f({op: 1, payload: [1, 2, 3]}) == 1
