import whiley.lang.*

type nat is (int x) where x >= 0

type dict is {int=>nat}

function f(int key, dict d) -> nat:
    return d[key]

public method main(System.Console sys) -> void:
    {int=>int} d = {-2=>20, -1=>10, 0=>0, 1=>10, 2=>20}
    sys.out.println(f(-2, d))
    sys.out.println(f(-1, d))
    sys.out.println(f(-0, d))
    sys.out.println(f(1, d))
    sys.out.println(f(2, d))
