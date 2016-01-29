

type Func is {
    function (int)->int read
}

function id(int x) -> int:
    return x

function test(Func f, int arg) -> int:
    return f.read(arg)

public export method test() :
    int x = test({read: &id}, 123)
    assume x == 123
    x = test({read: &id}, 12545)
    assume x == 12545
    x = test({read: &id}, -11)
    assume x == -11
