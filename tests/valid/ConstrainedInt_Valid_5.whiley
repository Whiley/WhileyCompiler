

type nat is int

function f() -> nat:
    return 1

public export method test() :
    assume f() == 1
