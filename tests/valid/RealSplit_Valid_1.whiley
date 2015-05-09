

function f(real z) -> (int, int):
    int x / int y = z
    return (x, y)

public export method test() -> void:
    assume f(10.0 / 5.0) == (2,1)   
    assume f(10.0 / 4.0) == (5,2)
    assume f(1.0 / 4.0) == (1,4)
    assume f(103.0 / 2.0) == (103,2)
    assume f(-10.0 / 5.0) == (-2,1)
    assume f(-10.0 / 4.0) == (-5,2)
    assume f(-1.0 / 4.0) == (-1,4)
    assume f(-103.0 / 2.0) == (-103,2)
    assume f(-10.0 / -5.0) == (2,1)
    assume f(-10.0 / -4.0) == (5,2)
    assume f(-1.0 / -4.0) == (1,4)
    assume f(-103.0 / -2.0) == (103,2)
    assume f(10.0 / -5.0) == (-2,1)
    assume f(10.0 / -4.0) == (-5,2)
    assume f(1.0 / -4.0) == (-1,4)
    assume f(103.0 / -2.0) == (-103,2)
