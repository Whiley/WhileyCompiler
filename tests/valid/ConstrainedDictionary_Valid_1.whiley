

type nat is (int x) where x >= 0

type dict is {int=>nat}

function f(int key, dict d) -> nat:
    return d[key]

public export method test() -> void:
    {int=>int} d = {-2=>20, -1=>10, 0=>0, 1=>10, 2=>20}
    assume f(-2, d) == 20
    assume f(-1, d) == 10
    assume f(-0, d) == 0
    assume f(1, d) == 10
    assume f(2, d) == 20
