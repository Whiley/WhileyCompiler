

method get() -> int:
    return 1

method f() -> int[]:
    return [1, 2, 3, get()]

public export method test() :
    &{int state} proc = new {state: 1}
    assume f() == [1,2,3,1]
