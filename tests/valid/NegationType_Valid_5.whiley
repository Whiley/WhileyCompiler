function f() -> int[] | int:
    return 1

public export method test() :
    assume f() == 1
