import whiley.lang.System

type LinkedList is null | {LinkedList next, int data}

type UnitList is {null next, int data}

type InterList is UnitList & LinkedList

function f(InterList l) => int:
    return l.data

method main(System.Console sys) => void:
    list = {next: null, data: 1234}
    d = f(list)
    sys.out.println("GOT: " ++ Any.toString(d))
