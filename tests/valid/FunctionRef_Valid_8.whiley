

function read(int a) -> int:
    return -a

function id(int x) -> int:
    return x

function test(function (int)->int read, int arg) -> int:
    return read(arg)

public export method test() :
    int x = test(&id, 1)
    assume x == 1
    x = test(&id, 123)
    assume x == 123
    x = test(&id, 223)
    assume x == 223
