import println from whiley.lang.System

define LinkedList as null | {int data, LinkedList next}
define UnitList as {int data, null next}

define InterList as UnitList & LinkedList

int f(InterList l):
    return l.data

void ::main(System.Console sys):
    list = { data: 1234, next: null}
    d = f(list)
    sys.out.println("GOT: " + Any.toString(d))
