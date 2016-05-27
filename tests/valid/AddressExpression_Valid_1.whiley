type fun is function(int)->int

function suc(int i) -> int:
    return i + 1

public export method test():
    fun x = &suc
    assume x(41) == 42
