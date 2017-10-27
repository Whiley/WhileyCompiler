

function f(int[] x) -> int[] | int:
    return x

public export method test() :
    assume f("Hello World") == "Hello World"
