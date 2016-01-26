

type Proc is &{
    function func(int) -> int
}

method func(Proc this, int x) -> int:
    return x + 1

public export method test(Proc this, int arg) -> int:
    return (*this).func(arg)

function id(int x) -> int:
    return x

public export method test() :
    Proc p = new {func: &id}
    int x = test(p, 123)
    assume x == 123
    x = test(p, 12545)
    assume x == 12545
    x = test(p,-11)
    assume x == -11
