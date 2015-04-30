function read(real a) -> real:
    return -a

function id(int x) -> int:
    return x

function test(function read(int)->int, real arg) -> real:
    return read(arg)
