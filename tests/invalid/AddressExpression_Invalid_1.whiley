type fun is function(int)->int

function overloaded(int i) -> int:
    return i + 1

function overloaded(bool b) -> bool:
    return !b

public export method test():
    fun x = &overloaded
