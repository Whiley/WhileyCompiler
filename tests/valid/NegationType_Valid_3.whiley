

function f(int[] x) -> !null & !int:
    return x

public export method test() :
    assume f("Hello World") == "Hello World"
