

function f(int x) -> (int, int):
    return (x, x + 2)

public export method test() -> void:
    (int,int) x = f(1)
    assume x == (1,3)
