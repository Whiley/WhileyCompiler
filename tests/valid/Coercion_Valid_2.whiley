

function f(int[] x) -> int[]:
    return x

public export method test() :
    assume f("Hello World") == [72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100]
