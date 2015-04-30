
type nat is (int n) where n >= 0

type LinkedList is int | {LinkedList next, int data}

type posLink is {posList next, nat data}

type posList is int | posLink

function f(LinkedList list) -> posList:
    return list
