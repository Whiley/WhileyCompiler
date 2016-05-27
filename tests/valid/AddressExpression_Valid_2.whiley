type fun1 is function(int)->int
type fun2 is function(bool)->bool

function overloaded(int a) -> int:
    return a + 1

function overloaded(bool a) -> bool:
    return !a

public export method test():
    fun1 x = &overloaded(int)
    assume x(41) == 42
    fun2 y = &overloaded(bool)
    assume y(true) == false
