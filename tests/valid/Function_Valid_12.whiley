

type msg is {int op, int s}

function f(msg m) -> int:
    return m.op + m.s

function f(int[] ls) -> int[]:
    return ls

function f(bool[] ls) -> bool[]:
    return ls

public export method test() :
    assume f([1, 2, 3]) == [1,2,3]
    assume f([true,false,true]) == [true,false,true]
