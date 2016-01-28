function read(bool a) -> bool:
    return !a

function id(int x) -> int:
    return x

function test(function read(int)->int, bool arg) -> bool:
    return read(arg)
