

function f(int[] x) -> ((int[]|bool) & (int[]|int) r):
    return x

public export method test() :
    assume f("Hello World") == "Hello World"
