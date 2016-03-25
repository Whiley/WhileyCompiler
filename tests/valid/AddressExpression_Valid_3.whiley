type fun is function(int,int)->int

function add(int a, int b) -> int:
    return a + b

public export method test():
    fun x = &add
    assume x(4, 5) == 9
