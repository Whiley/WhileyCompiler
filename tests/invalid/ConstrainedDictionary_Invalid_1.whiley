type nat is (int x) where x >= 0

type dict is {int=>nat}

function f(int key, dict d) -> int:
    return d[key]

public method g() -> int:
    dict d = {-2=>20, -1=>-1, 0=>0, 1=>10, 2=>20}
    return f(0,d)
