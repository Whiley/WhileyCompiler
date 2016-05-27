type fun1 is function(int,int)->int
type fun2 is function(bool,bool)->bool

function overloaded(int a, int b) -> int:
    return a + b

function overloaded(bool a, bool b) -> bool:
    return a && b

public export method test():
    fun1 x = &overloaded(int,int)
    assume x(4,5) == 9
    fun2 y = &overloaded(bool,bool)
    assume y(true,false) == false
